package CMS.sys.ai.service;

import CMS.sys.ai.dto.*;
import CMS.sys.dto.ContactRequestDTO;
import CMS.sys.dto.ContactResponseDTO;
import CMS.sys.entity.Contact;
import CMS.sys.entity.ContactInteraction;
import CMS.sys.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactAiService {

    private final ChatClient chatClient;
    private final ContactRepository contactRepository;

    public AiContactResponse generateContactSuggestion(AiContactRequest request) {
        Instant start = Instant.now();
        String systemPrompt = buildSystemPrompt();
        String userPrompt = buildUserPrompt(request);

        String generated = chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .content();

        long durationMs = Duration.between(start, Instant.now()).toMillis();
        log.info("AI suggestion generated in {} ms", durationMs);

        return AiContactResponse.builder()
                .response(generated)
                .model("qwen2.5-coder:1.5b")
                .durationMs(durationMs)
                .build();
    }

    public List<AiContactSearchResult> aiContactSearch(String query, List<Contact> contacts) {
        Instant start = Instant.now();
        String normalizedQuery = query.toLowerCase(Locale.ROOT);
        Pattern termPattern = Pattern.compile("[a-zA-Z0-9@.]+");
        Matcher matcher = termPattern.matcher(normalizedQuery);
        List<String> terms = new ArrayList<>();
        while (matcher.find()) terms.add(matcher.group());

        List<AiContactSearchResult> results = contacts.stream()
                .map(contact -> scoreContact(contact, terms))
                .filter(r -> r.getRelevanceScore() > 0.0)
                .sorted((a, b) -> Double.compare(b.getRelevanceScore(), a.getRelevanceScore()))
                .toList();

        long durationMs = Duration.between(start, Instant.now()).toMillis();
        log.info("AI contact search completed in {} ms with {} results", durationMs, results.size());
        return results;
    }

    public List<AiDuplicateResult> detectDuplicates(List<Contact> contacts) {
        Instant start = Instant.now();
        List<AiDuplicateResult> results = new ArrayList<>();
        List<Contact> normalized = contacts.stream()
                .map(this::normalizeForComparison)
                .toList();

        for (int i = 0; i < normalized.size(); i++) {
            Contact first = normalized.get(i);
            List<String> duplicates = new ArrayList<>();
            List<String> reasons = new ArrayList<>();
            for (int j = i + 1; j < normalized.size(); j++) {
                Contact second = normalized.get(j);
                double similarity = computeSimilarity(first, second);
                if (similarity > 0.75) {
                    duplicates.add(second.getId());
                    reasons.add("Similar contact detected");
                }
            }
            if (!duplicates.isEmpty()) {
                results.add(AiDuplicateResult.builder()
                        .sourceContactId(first.getId())
                        .probableDuplicateIds(duplicates)
                        .similarityScore(0.75)
                        .reason(String.join("; ", reasons))
                        .build());
            }
        }

        long durationMs = Duration.between(start, Instant.now()).toMillis();
        log.info("Duplicate detection completed in {} ms with {} candidates", durationMs, results.size());
        return results;
    }

    public AiEnrichedContactResult enrichContact(Contact contact, List<ContactInteraction> interactions) {
        Instant start = Instant.now();
        String contactSummary = buildContactSummary(contact, interactions);
        String userPrompt = """
                Given this contact summary, suggest:
                - smart tags as a JSON array
                - a short contact note
                - one follow-up suggestion
                Return JSON only in this shape:
                {"tags":["tag1","tag2"],"notes":"...","followUp":"..."}
                Contact summary:
                """ + contactSummary;

        String generated = chatClient.prompt()
                .system("You are a helpful CRM assistant. Respond only with JSON.")
                .user(userPrompt)
                .call()
                .content();

        AiEnrichedContactResult.AiEnrichedContactResultBuilder builder = AiEnrichedContactResult.builder()
                .contactId(contact.getId());

        try {
            String trimmed = generated.trim();
            String json = trimmed.contains("{") ? trimmed.substring(trimmed.indexOf("{")) : trimmed;
            Map<String, Object> parsed = new com.fasterxml.jackson.databind.ObjectMapper().readValue(json, Map.class);
            List<String> tags = ((List<?>) parsed.getOrDefault("tags", List.of())).stream()
                    .map(Object::toString)
                    .toList();
            builder.suggestedTags(tags);
            builder.generatedNotes(Objects.toString(parsed.getOrDefault("notes", ""), ""));
            builder.followUpSuggestion(Objects.toString(parsed.getOrDefault("followUp", ""), ""));
        } catch (Exception e) {
            log.warn("Failed to parse AI enrichment JSON for contact {}", contact.getId(), e);
            builder.suggestedTags(List.of());
            builder.generatedNotes(generated);
            builder.followUpSuggestion("");
        }

        long durationMs = Duration.between(start, Instant.now()).toMillis();
        log.info("Enriched contact {} in {} ms", contact.getId(), durationMs);
        return builder.build();
    }

    public String generateMeetingSummary(String notes) {
        if (notes == null || notes.isBlank()) return "";
        Instant start = Instant.now();
        String userPrompt = """
                Summarize this meeting or call note into concise outcomes and action items.
                Notes:
                """ + notes;

        String summary = chatClient.prompt()
                .system("You are a concise meeting assistant. Return short bullet-style outcomes.")
                .user(userPrompt)
                .call()
                .content();

        long durationMs = Duration.between(start, Instant.now()).toMillis();
        log.info("Meeting summary generated in {} ms", durationMs);
        return summary;
    }

    public AiParsedContactResponse parseBusinessCard(String text) {
        Instant start = Instant.now();
        String userPrompt = """
                Extract contact fields from this text as JSON using this schema:
                {"firstName":"","lastName":"","email":"","mobileNumber":"","company":"","designation":"","address":""}
                Text:
                """ + text;

        String generated = chatClient.prompt()
                .system("You are a data extraction assistant. Respond only with JSON.")
                .user(userPrompt)
                .call()
                .content();

        ContactRequestDTO contact = ContactRequestDTO.builder().build();
        double confidence = 0.8;
        try {
            String trimmed = generated.trim();
            String json = trimmed.contains("{") ? trimmed.substring(trimmed.indexOf("{")) : trimmed;
            Map<String, Object> parsed = new com.fasterxml.jackson.databind.ObjectMapper().readValue(json, Map.class);
            contact = ContactRequestDTO.builder()
                    .firstName(Objects.toString(parsed.get("firstName"), null))
                    .lastName(Objects.toString(parsed.get("lastName"), null))
                    .email(Objects.toString(parsed.get("email"), null))
                    .mobileNumber(Objects.toString(parsed.get("mobileNumber"), null))
                    .company(Objects.toString(parsed.get("company"), null))
                    .designation(Objects.toString(parsed.get("designation"), null))
                    .address(Objects.toString(parsed.get("address"), null))
                    .build();
            confidence = contact.getFirstName() != null ? 0.9 : 0.6;
        } catch (Exception e) {
            log.warn("Failed to parse OCR/vendor text into contact JSON", e);
        }

        long durationMs = Duration.between(start, Instant.now()).toMillis();
        log.info("Parsed business-card-like text in {} ms", durationMs);
        return AiParsedContactResponse.builder()
                .contact(contact)
                .confidence(confidence)
                .rawExtract(text)
                .build();
    }

    public AiParsedContactResponse parseVoiceSearchTranscript(String transcript) {
        Instant start = Instant.now();
        AiParsedContactResponse parsed = parseBusinessCard(transcript);
        long durationMs = Duration.between(start, Instant.now()).toMillis();
        log.info("Voice search transcript parsed in {} ms with confidence {}", durationMs, parsed.getConfidence());
        return parsed;
    }

    public String generateSmartNotes(ContactResponseDTO contact, List<ContactInteraction> interactions) {
        Instant start = Instant.now();
        StringBuilder context = new StringBuilder();
        context.append("Contact: ").append(contact.getFirstName()).append(" ").append(contact.getLastName()).append("\n");
        context.append("Company: ").append(contact.getCompany()).append("\n");
        context.append("Notes: ").append(contact.getNotes()).append("\n");
        if (!interactions.isEmpty()) {
            context.append("Recent interactions:\n");
            interactions.stream().limit(5).forEach(i -> context.append("- ")
                    .append(i.getType()).append(": ").append(i.getSubject()).append("\n"));
        }

        String prompt = "Generate concise CRM notes and key takeaways from this contact profile.\n" + context;

        String generated = chatClient.prompt()
                .system("You are a CRM assistant. Return concise notes only.")
                .user(prompt)
                .call()
                .content();

        long durationMs = Duration.between(start, Instant.now()).toMillis();
        log.info("Smart notes generated in {} ms", durationMs);
        return generated;
    }

    public AiFollowUpResponse suggestFollowUp(Contact contact, List<ContactInteraction> interactions) {
        Instant start = Instant.now();
        String recent = interactions.stream()
                .limit(5)
                .map(i -> i.getType() + ": " + i.getSubject() + " | outcome=" + i.getOutcome())
                .collect(Collectors.joining("\n"));

        String prompt = """
                Based on this contact and recent interactions, suggest one follow-up action and ideal due date.
                Respond in JSON: {"suggestion":"...","suggestedDueDate":"YYYY-MM-DD","relatedContactIds":["..."]}
                Contact:
                """ + buildContactSummary(contact, interactions) + "\nRecent interactions:\n" + recent;

        String generated = chatClient.prompt()
                .system("You are a helpful CRM assistant. Respond only with JSON.")
                .user(prompt)
                .call()
                .content();

        AiFollowUpResponse response = AiFollowUpResponse.builder()
                .suggestion("")
                .confidence(0.75)
                .suggestedDueDate(null)
                .relatedContactIds(List.of(contact.getId()))
                .matchedInteractionTypes(interactions.stream().map(ContactInteraction::getType).toList())
                .build();

        try {
            String trimmed = generated.trim();
            String json = trimmed.contains("{") ? trimmed.substring(trimmed.indexOf("{")) : trimmed;
            Map<String, Object> parsed = new com.fasterxml.jackson.databind.ObjectMapper().readValue(json, Map.class);
            response.setSuggestion(Objects.toString(parsed.get("suggestion"), ""));
            response.setSuggestedDueDate(Objects.toString(parsed.get("suggestedDueDate"), null));
            @SuppressWarnings("unchecked")
            List<Object> related = (List<Object>) parsed.getOrDefault("relatedContactIds", List.of());
            response.setRelatedContactIds(related.stream().map(Object::toString).toList());
            response.setConfidence(0.9);
        } catch (Exception e) {
            log.warn("Failed to parse follow-up suggestion JSON for contact {}", contact.getId(), e);
        }

        long durationMs = Duration.between(start, Instant.now()).toMillis();
        log.info("Follow-up suggestion generated in {} ms", durationMs);
        return response;
    }

    private String buildSystemPrompt() {
        return """
                You are an intelligent assistant for a Contact Management System.
                You help users with contact-related tasks such as:
                - Suggesting professional email signatures
                - Improving contact notes
                - Recommending contact organization strategies
                - Answering questions about contact management best practices
                Keep responses concise, professional, and actionable.
                """;
    }

    private String buildUserPrompt(AiContactRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("User request: ").append(request.getPrompt());
        if (request.getContext() != null && !request.getContext().isBlank()) {
            prompt.append("\nContext: ").append(request.getContext());
        }
        return prompt.toString();
    }

    private String buildContactSummary(Contact contact, List<ContactInteraction> interactions) {
        StringBuilder summary = new StringBuilder();
        summary.append(contact.getFirstName()).append(" ").append(contact.getLastName()).append("\n");
        summary.append("Company: ").append(contact.getCompany()).append("\n");
        summary.append("Email: ").append(contact.getEmail()).append("\n");
        summary.append("Notes: ").append(contact.getNotes()).append("\n");
        if (!interactions.isEmpty()) {
            summary.append("Recent interactions:\n");
            interactions.stream().limit(5)
                    .forEach(i -> summary.append("- ")
                            .append(i.getType()).append(": ")
                            .append(i.getSubject()).append("\n"));
        }
        return summary.toString();
    }

    private AiContactSearchResult scoreContact(Contact contact, List<String> terms) {
        List<String> fields = List.of(
                contact.getFirstName(),
                contact.getLastName(),
                contact.getNickname(),
                contact.getEmail(),
                contact.getCompany(),
                contact.getDesignation(),
                contact.getCity(),
                contact.getCountry(),
                contact.getNotes()
        );
        String joined = String.join(" ", fields).toLowerCase(Locale.ROOT);
        long matches = terms.stream().filter(joined::contains).count();
        double score = terms.isEmpty() ? 0.0 : (double) matches / terms.size();
        List<String> matchedFields = fields.stream()
                .filter(Objects::nonNull)
                .filter(f -> terms.stream().anyMatch(f.toLowerCase(Locale.ROOT)::contains))
                .toList();
        return AiContactSearchResult.builder()
                .contact(contact)
                .relevanceScore(score)
                .matchedFields(matchedFields)
                .build();
    }

    private Contact normalizeForComparison(Contact contact) {
        return Contact.builder()
                .id(contact.getId())
                .firstName(contact.getFirstName() == null ? "" : contact.getFirstName().toLowerCase(Locale.ROOT))
                .lastName(contact.getLastName() == null ? "" : contact.getLastName().toLowerCase(Locale.ROOT))
                .email(contact.getEmail() == null ? "" : contact.getEmail().toLowerCase(Locale.ROOT))
                .mobileNumber(contact.getMobileNumber() == null ? "" : contact.getMobileNumber().toLowerCase(Locale.ROOT))
                .company(contact.getCompany() == null ? "" : contact.getCompany().toLowerCase(Locale.ROOT))
                .build();
    }

    private double computeSimilarity(Contact a, Contact b) {
        if (Objects.equals(a.getEmail(), b.getEmail()) && !a.getEmail().isBlank()) return 0.95;
        if (Objects.equals(a.getMobileNumber(), b.getMobileNumber()) && !a.getMobileNumber().isBlank()) return 0.9;
        double nameSimilarity = jaccard(a.getFirstName() + " " + a.getLastName(), b.getFirstName() + " " + b.getLastName());
        double companySimilarity = jaccard(a.getCompany(), b.getCompany());
        return Math.max(nameSimilarity, companySimilarity);
    }

    private double jaccard(String a, String b) {
        if (a == null || b == null || a.isBlank() || b.isBlank()) return 0.0;
        Set<String> setA = new HashSet<>(List.of(a.split("\\s+")));
        Set<String> setB = new HashSet<>(List.of(b.split("\\s+")));
        Set<String> intersection = new HashSet<>(setA);
        intersection.retainAll(setB);
        Set<String> union = new HashSet<>(setA);
        union.addAll(setB);
        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }
}