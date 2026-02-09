CREATE DATABASE IF NOT EXISTS oceanview_resort;
USE oceanview_resort;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  full_name VARCHAR(120) NOT NULL,
  email VARCHAR(120) UNIQUE,
  role VARCHAR(30) NOT NULL,
  is_active TINYINT(1) NOT NULL DEFAULT 1,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS guests (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  first_name VARCHAR(80) NOT NULL,
  last_name VARCHAR(80) NOT NULL,
  email VARCHAR(120),
  phone VARCHAR(30) NOT NULL,
  address VARCHAR(255) NOT NULL,
  id_type VARCHAR(40),
  id_number VARCHAR(60) UNIQUE,
  nationality VARCHAR(60),
  guest_type VARCHAR(30) NOT NULL DEFAULT 'REGULAR',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS room_types (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  type_name VARCHAR(80) NOT NULL UNIQUE,
  description VARCHAR(255),
  rate_per_night DECIMAL(10, 2) NOT NULL,
  max_occupancy INT NOT NULL,
  amenities VARCHAR(255),
  is_active TINYINT(1) NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS rooms (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  room_number VARCHAR(20) NOT NULL UNIQUE,
  room_type_id BIGINT NOT NULL,
  floor INT NOT NULL,
  status VARCHAR(30) NOT NULL,
  description VARCHAR(255),
  is_full_access TINYINT(1) NOT NULL DEFAULT 0,
  FOREIGN KEY (room_type_id) REFERENCES room_types(id)
);

CREATE TABLE IF NOT EXISTS reservations (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  reservation_no VARCHAR(40) NOT NULL UNIQUE,
  guest_id BIGINT NOT NULL,
  room_id BIGINT NOT NULL,
  check_in DATE NOT NULL,
  check_out DATE NOT NULL,
  status VARCHAR(30) NOT NULL,
  created_by BIGINT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (guest_id) REFERENCES guests(id),
  FOREIGN KEY (room_id) REFERENCES rooms(id),
  FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS bills (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  bill_no VARCHAR(40) NOT NULL UNIQUE,
  reservation_id BIGINT NOT NULL,
  number_of_nights INT NOT NULL,
  room_rate DECIMAL(10, 2) NOT NULL,
  total_amount DECIMAL(10, 2) NOT NULL,
  discount_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
  tax_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
  net_amount DECIMAL(10, 2) NOT NULL,
  generated_by BIGINT NOT NULL,
  generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (reservation_id) REFERENCES reservations(id),
  FOREIGN KEY (generated_by) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS reports (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  report_type VARCHAR(40) NOT NULL,
  format VARCHAR(20) NOT NULL,
  generated_by BIGINT NOT NULL,
  generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  parameters TEXT,
  content LONGBLOB,
  FOREIGN KEY (generated_by) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS discounts (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(120) NOT NULL,
  discount_type VARCHAR(30) NOT NULL,
  guest_type VARCHAR(30),
  percent DECIMAL(5, 2) NOT NULL,
  description VARCHAR(255),
  is_active TINYINT(1) NOT NULL DEFAULT 1,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------------------------
-- Advanced database features: business rules in the database
-- ---------------------------------------------------------------------------

-- Audit log for reservation lifecycle (business rule: accountability)
CREATE TABLE IF NOT EXISTS reservation_audit_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  reservation_id BIGINT NOT NULL,
  reservation_no VARCHAR(40) NOT NULL,
  action VARCHAR(20) NOT NULL,
  performed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (reservation_id) REFERENCES reservations(id)
);

DELIMITER //
-- Trigger: log every new reservation (business rule: audit trail)
DROP TRIGGER IF EXISTS trg_reservation_after_insert//
CREATE TRIGGER trg_reservation_after_insert
  AFTER INSERT ON reservations
  FOR EACH ROW
BEGIN
  INSERT INTO reservation_audit_log (reservation_id, reservation_no, action)
  VALUES (NEW.id, NEW.reservation_no, 'CREATED');
END//

-- Stored procedure: return room IDs available for a date range (business rule: no double booking)
DROP PROCEDURE IF EXISTS get_available_room_ids//
CREATE PROCEDURE get_available_room_ids(IN p_check_in DATE, IN p_check_out DATE)
BEGIN
  SELECT r.id
  FROM rooms r
  WHERE r.id NOT IN (
    SELECT res.room_id
    FROM reservations res
    WHERE res.status NOT IN ('CANCELLED')
      AND (res.check_in < p_check_out AND res.check_out > p_check_in)
  )
  ORDER BY r.room_number;
END//

DELIMITER ;
