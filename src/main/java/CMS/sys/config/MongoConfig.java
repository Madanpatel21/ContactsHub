package CMS.sys.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * Configuration class for MongoDB-specific settings.
 * Enables auditing features for automatic timestamp management.
 * 
 * Design Considerations:
 * - Enables @CreatedDate and @LastModifiedDate annotations
 * - Prepares for future MongoDB configurations (indexes, converters, etc.)
 * - Ready for future enhancements like MongoDB triggers or change streams
 * 
 * @author Contact Management System
 * @version 1.0
 */
@Configuration
@EnableMongoAuditing
public class MongoConfig {
}