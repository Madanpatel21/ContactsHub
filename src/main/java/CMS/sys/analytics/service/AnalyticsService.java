package CMS.sys.analytics.service;

import CMS.sys.analytics.dto.*;
import CMS.sys.entity.Contact;
import CMS.sys.entity.ContactInteraction;
import CMS.sys.interaction.repository.ContactInteractionRepository;
import CMS.sys.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final ContactRepository contactRepository;
    private final ContactInteractionRepository interactionRepository;

    public List<ContactFrequencyDTO> getMostContacted(int limit) {
        return getContactFrequencies().stream()
                .sorted((a, b) -> Long.compare(b.getInteractionCount(), a.getInteractionCount()))
                .limit(limit)
                .toList();
    }

    public List<ContactFrequencyDTO> getLeastContacted(int limit) {
        List<ContactFrequencyDTO> all = getContactFrequencies();
        if (all.size() <= limit) return all;
        return all.stream()
                .sorted(Comparator.comparingLong(ContactFrequencyDTO::getInteractionCount))
                .limit(limit)
                .toList();
    }

    public List<CompanyDistributionDTO> getContactsByCompany() {
        List<Contact> contacts = contactRepository.findAll();
        return contacts.stream()
                .filter(c -> c.getCompany() != null && !c.getCompany().isBlank())
                .collect(Collectors.groupingBy(Contact::getCompany))
                .entrySet().stream()
                .map(entry -> CompanyDistributionDTO.builder()
                        .company(entry.getKey())
                        .contactCount(entry.getValue().size())
                        .build())
                .sorted((a, b) -> Long.compare(b.getContactCount(), a.getContactCount()))
                .toList();
    }

    public List<CityDistributionDTO> getContactsByCity() {
        List<Contact> contacts = contactRepository.findAll();
        return contacts.stream()
                .filter(c -> c.getCity() != null && !c.getCity().isBlank())
                .collect(Collectors.groupingBy(Contact::getCity))
                .entrySet().stream()
                .map(entry -> CityDistributionDTO.builder()
                        .city(entry.getKey())
                        .contactCount(entry.getValue().size())
                        .build())
                .sorted((a, b) -> Long.compare(b.getContactCount(), a.getContactCount()))
                .toList();
    }

    public List<MonthlyAdditionsDTO> getMonthlyAdditions() {
        List<Contact> contacts = contactRepository.findAll();
        Map<String, Long> byMonth = contacts.stream()
                .filter(c -> c.getCreatedAt() != null)
                .collect(Collectors.groupingBy(c -> c.getCreatedAt().toLocalDate()
                        .withDayOfMonth(1)
                        .toString(),
                        TreeMap::new,
                        Collectors.counting()));
        return byMonth.entrySet().stream()
                .map(entry -> MonthlyAdditionsDTO.builder()
                        .month(entry.getKey())
                        .count(entry.getValue())
                        .build())
                .toList();
    }

    public List<InteractionHeatmapDTO> getInteractionHeatmap() {
        List<ContactInteraction> interactions = interactionRepository.findAll();
        Map<String, long[]> buckets = new HashMap<>();
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (int d = 0; d < 7; d++) {
            for (int h = 0; h < 24; h++) {
                buckets.put(days[d] + "|" + h, new long[]{0});
            }
        }
        for (ContactInteraction interaction : interactions) {
            if (interaction.getLastContactedDate() == null) continue;
            LocalDateTime ldt = interaction.getLastContactedDate();
            DayOfWeek dow = ldt.getDayOfWeek();
            int hour = ldt.getHour();
            String key = dow.name().charAt(0) + dow.name().substring(1).toLowerCase(Locale.ROOT) + "|" + hour;
            buckets.computeIfAbsent(key, k -> new long[]{0})[0]++;
        }
        List<InteractionHeatmapDTO> result = new ArrayList<>();
        for (Map.Entry<String, long[]> entry : buckets.entrySet()) {
            String[] parts = entry.getKey().split("\\|");
            result.add(InteractionHeatmapDTO.builder()
                    .dayOfWeek(parts[0])
                    .hour(Integer.parseInt(parts[1]))
                    .count(entry.getValue()[0])
                    .build());
        }
        return result.stream()
                .sorted(Comparator.comparing(InteractionHeatmapDTO::getDayOfWeek)
                        .thenComparingInt(InteractionHeatmapDTO::getHour))
                .toList();
    }

    public CompletionRateDTO getReminderCompletionRate() {
        List<ContactInteraction> interactions = interactionRepository.findAll();
        long total = interactions.stream()
                .filter(i -> i.getType() == ContactInteraction.InteractionType.FOLLOW_UP)
                .count();
        long completed = total;
        double percentage = total == 0 ? 0.0 : (completed * 100.0) / total;
        return CompletionRateDTO.builder()
                .total(total)
                .completed(completed)
                .completionPercentage(Math.round(percentage * 100.0) / 100.0)
                .build();
    }

    public Map<String, Object> getOverview() {
        Map<String, Object> overview = new LinkedHashMap<>();
        overview.put("totalContacts", contactRepository.count());
        overview.put("totalInteractions", interactionRepository.count());
        overview.put("totalFollowUps", interactionRepository.findAll().stream()
                .filter(i -> i.getType() == ContactInteraction.InteractionType.FOLLOW_UP)
                .count());
        overview.put("mostContacted", getMostContacted(5));
        overview.put("leastContacted", getLeastContacted(5));
        overview.put("contactsByCompany", getContactsByCompany());
        overview.put("contactsByCity", getContactsByCity());
        overview.put("monthlyAdditions", getMonthlyAdditions());
        overview.put("interactionHeatmap", getInteractionHeatmap());
        overview.put("reminderCompletionRate", getReminderCompletionRate());
        return overview;
    }

    private List<ContactFrequencyDTO> getContactFrequencies() {
        List<Contact> contacts = contactRepository.findAll();
        Map<String, ContactFrequencyDTO> frequencyMap = new HashMap<>();
        for (Contact contact : contacts) {
            frequencyMap.put(contact.getId(),
                    ContactFrequencyDTO.builder()
                            .contactId(contact.getId())
                            .contactName(contact.getFirstName() + " " + contact.getLastName())
                            .company(contact.getCompany())
                            .interactionCount(0)
                            .build());
        }
        List<ContactInteraction> interactions = interactionRepository.findAll();
        for (ContactInteraction interaction : interactions) {
            ContactFrequencyDTO dto = frequencyMap.get(interaction.getContactId());
            if (dto != null) {
                dto.setInteractionCount(dto.getInteractionCount() + 1);
                if (interaction.getLastContactedDate() != null) {
                    String date = interaction.getLastContactedDate().toLocalDate().toString();
                    if (dto.getLastInteractionDate() == null || date.compareTo(dto.getLastInteractionDate()) > 0) {
                        dto.setLastInteractionDate(date);
                    }
                }
            }
        }
        return new ArrayList<>(frequencyMap.values());
    }
}