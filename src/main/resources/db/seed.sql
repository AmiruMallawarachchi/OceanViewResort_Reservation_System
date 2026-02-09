USE oceanview_resort;

INSERT INTO users (username, password_hash, full_name, email, role, is_active)
VALUES
  ('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'System Admin', 'admin@oceanviewresort.com', 'ADMIN', 1),
  ('res1', '6706cff203f098b49f4e628d06daf2f03b529f67a5325e9421e3830d04e180dd', 'Front Desk Staff', 'frontdesk@oceanviewresort.com', 'RESERVATIONIST', 1)
ON DUPLICATE KEY UPDATE
  password_hash = VALUES(password_hash),
  full_name = VALUES(full_name),
  email = VALUES(email),
  role = VALUES(role),
  is_active = VALUES(is_active);

INSERT INTO room_types (type_name, description, rate_per_night, max_occupancy, amenities, is_active)
VALUES
  ('Coral Standard', 'Cozy garden-side room with modern essentials', 110.00, 2, 'WiFi, AC', 1),
  ('Lagoon Deluxe', 'Bright room with lagoon balcony and seating nook', 175.00, 3, 'WiFi, AC, Balcony, Hot Water', 1),
  ('Sunset Family', 'Spacious family room with flexible bedding', 210.00, 4, 'WiFi, AC, Sofa Bed, Microwave, Hot Water', 1),
  ('Ocean Suite', 'Premium suite with panoramic ocean view', 320.00, 4, 'WiFi, AC, Ocean View, Lounge, Hot Water, Bathtub', 1),
  ('Skyline Penthouse', 'Top-floor penthouse with private terrace', 520.00, 6, 'WiFi, AC, Private Terrace, Kitchenette, Hot Water, Jacuzzi, Bathtub', 1)
ON DUPLICATE KEY UPDATE
  description = VALUES(description),
  rate_per_night = VALUES(rate_per_night),
  max_occupancy = VALUES(max_occupancy),
  amenities = VALUES(amenities),
  is_active = VALUES(is_active);

INSERT IGNORE INTO rooms (room_number, room_type_id, floor, status, description, is_full_access)
SELECT
  CONCAT(f.floor, LPAD(r.room_pos, 2, '0')) AS room_number,
  rt.id AS room_type_id,
  f.floor,
  'AVAILABLE' AS status,
  CONCAT(rt.type_name, ' room on floor ', f.floor) AS description,
  CASE WHEN f.floor = 10 AND r.room_pos IN (1, 2) THEN 1 ELSE 0 END AS is_full_access
FROM
  (SELECT 1 AS floor UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5
   UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10) f
CROSS JOIN
  (SELECT 1 AS room_pos UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
   UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8) r
JOIN
  room_types rt
  ON rt.type_name =
    CASE
      WHEN f.floor IN (1, 2) THEN 'Coral Standard'
      WHEN f.floor IN (3, 4) THEN 'Lagoon Deluxe'
      WHEN f.floor IN (5, 6) THEN 'Sunset Family'
      WHEN f.floor IN (7, 8, 9) THEN 'Ocean Suite'
      ELSE 'Skyline Penthouse'
    END;

UPDATE rooms r
JOIN room_types rt
  ON rt.type_name =
    CASE
      WHEN r.floor IN (1, 2) THEN 'Coral Standard'
      WHEN r.floor IN (3, 4) THEN 'Lagoon Deluxe'
      WHEN r.floor IN (5, 6) THEN 'Sunset Family'
      WHEN r.floor IN (7, 8, 9) THEN 'Ocean Suite'
      ELSE 'Skyline Penthouse'
    END
SET
  r.room_type_id = rt.id,
  r.status = 'AVAILABLE',
  r.description = CONCAT(rt.type_name, ' room on floor ', r.floor),
  r.is_full_access = CASE WHEN r.room_number IN ('1001', '1002') THEN 1 ELSE 0 END
WHERE r.floor BETWEEN 1 AND 10;

INSERT INTO guests (first_name, last_name, email, phone, address, id_type, id_number, nationality, guest_type)
VALUES
  ('Amaya', 'Perera', 'amaya@example.com', '+94 71 123 4567', 'Galle, Sri Lanka', 'NIC', '987654321V', 'Sri Lankan', 'VIP'),
  ('Nimal', 'Silva', 'nimal@example.com', '+94 77 987 6543', 'Colombo, Sri Lanka', 'NIC', '901234567V', 'Sri Lankan', 'REGULAR')
ON DUPLICATE KEY UPDATE
  first_name = VALUES(first_name),
  last_name = VALUES(last_name),
  email = VALUES(email),
  phone = VALUES(phone),
  address = VALUES(address),
  id_type = VALUES(id_type),
  nationality = VALUES(nationality),
  guest_type = VALUES(guest_type);

INSERT INTO discounts (name, discount_type, guest_type, percent, description, is_active)
VALUES
  ('VIP Guest Discount', 'GUEST_TYPE', 'VIP', 15.00, 'Automatic VIP discount', 1),
  ('Corporate Guest Discount', 'GUEST_TYPE', 'CORPORATE', 10.00, 'Corporate rate discount', 1),
  ('Regular Guest Discount', 'GUEST_TYPE', 'REGULAR', 0.00, 'Standard guest rate', 1),
  ('Seasonal Offer', 'PROMOTION', NULL, 8.00, 'Seasonal promotional discount', 1),
  ('Vacation Special', 'PROMOTION', NULL, 12.00, 'Vacation promotional discount', 1);


