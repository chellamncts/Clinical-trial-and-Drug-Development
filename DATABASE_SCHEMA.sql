-- Clinical Trial & Drug Development Tracking System schema
-- Compatible with MySQL 8+

CREATE DATABASE IF NOT EXISTS clinical_trial;
USE clinical_trial;

CREATE TABLE IF NOT EXISTS trial_protocol (
	protocol_id INT AUTO_INCREMENT PRIMARY KEY,
	trial_title VARCHAR(255),
	therapeutic_area VARCHAR(100),
	phase ENUM('PHASE_I','PHASE_II','PHASE_III','PHASE_IV'),
	start_date DATE,
	protocol_status ENUM('DRAFT','APPROVED','ACTIVE','CLOSED')
);

CREATE TABLE IF NOT EXISTS trial_subject (
	subject_id INT AUTO_INCREMENT PRIMARY KEY,
	protocol_id INT,
	site_id INT,
	enrollment_date DATE,
	study_arm VARCHAR(50),
	subject_status ENUM('SCREENED','ENROLLED','COMPLETED','WITHDRAWN'),
	CONSTRAINT fk_subject_protocol
		FOREIGN KEY (protocol_id) REFERENCES trial_protocol(protocol_id)
);

CREATE TABLE IF NOT EXISTS visit_record (
	visit_id INT AUTO_INCREMENT PRIMARY KEY,
	subject_id INT,
	visit_name VARCHAR(100),
	visit_date DATE,
	crf_status ENUM('PENDING','COMPLETED','LOCKED'),
	query_count INT,
	CONSTRAINT fk_visit_subject
		FOREIGN KEY (subject_id) REFERENCES trial_subject(subject_id)
);

CREATE TABLE IF NOT EXISTS adverse_event (
	event_id INT AUTO_INCREMENT PRIMARY KEY,
	subject_id INT,
	event_description VARCHAR(500),
	event_onset_date DATE,
	severity ENUM('MILD','MODERATE','SEVERE'),
	event_status ENUM('REPORTED','UNDER_REVIEW','RESOLVED','FATAL'),
	CONSTRAINT fk_ae_subject
		FOREIGN KEY (subject_id) REFERENCES trial_subject(subject_id)
);

-- Module 5: Lab Sample & Investigational Product Tracking
CREATE TABLE IF NOT EXISTS sample_log (
	sample_id INT AUTO_INCREMENT PRIMARY KEY,
	subject_id INT,
	sample_type VARCHAR(50),
	collection_date DATE,
	lab_result VARCHAR(255),
	sample_status ENUM('COLLECTED','IN_TRANSIT','ANALYZED','DESTROYED'),
	custody_log VARCHAR(500),
	cold_chain_temperature_c DOUBLE,
	custody_status ENUM('COLLECTED','HANDED_OVER','IN_TRANSIT','RECEIVED_AT_LAB','COMPLETED'),
	CONSTRAINT fk_sample_subject
		FOREIGN KEY (subject_id) REFERENCES trial_subject(subject_id)
);

CREATE TABLE IF NOT EXISTS investigational_product_inventory (
	inventory_id INT AUTO_INCREMENT PRIMARY KEY,
	product_name VARCHAR(120) NOT NULL,
	batch_number VARCHAR(60) NOT NULL,
	quantity_received INT NOT NULL DEFAULT 0,
	quantity_dispensed INT NOT NULL DEFAULT 0,
	quantity_available INT NOT NULL DEFAULT 0,
	storage_temperature_c DOUBLE,
	cold_chain_status ENUM('OK','EXCURSION','UNKNOWN')
);

