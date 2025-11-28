-- Drop the old table if it exists
DROP TABLE IF EXISTS service_requests;

-- Create the new service_requests table
CREATE TABLE service_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_type VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    location VARCHAR(255) NOT NULL,
    requested_by VARCHAR(255) NOT NULL,
    request_date_time DATETIME NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    volunteers_needed INT NOT NULL DEFAULT 0,
    volunteers_accepted INT NOT NULL DEFAULT 0
);

-- Create the service_request_volunteers table for the many-to-many relationship
CREATE TABLE service_request_volunteers (
    service_request_id BIGINT NOT NULL,
    volunteer_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (service_request_id, volunteer_id),
    FOREIGN KEY (service_request_id) REFERENCES service_requests(id)
);

-- Add indexes for better query performance
CREATE INDEX idx_service_requests_status ON service_requests(status);
CREATE INDEX idx_service_requests_requested_by ON service_requests(requested_by);
CREATE INDEX idx_service_request_volunteers_volunteer ON service_request_volunteers(volunteer_id);
# Hibernate ddl auto (create, create-drop, validate, update)
spring.jpa.hibernate.ddl-auto=update

# Enable detailed logging for hibernate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE