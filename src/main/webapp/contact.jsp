<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Contact | OceanView Resort</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css?v=20260206" />
</head>
<body>
  <%@ include file="/WEB-INF/partials/nav.jspf" %>
  <main class="container">
    <div class="page-header">
      <div>
        <h1 class="page-header__title">Contact Us</h1>
        <p class="muted">We are here to help with reservations and system support.</p>
      </div>
      <span class="badge">Contact</span>
    </div>

    <section class="section">
      <div class="grid grid--3">
        <div class="card">
          <h3>Email Support</h3>
          <p>Send your request to <a href="mailto:support@oceanviewresort.com">support@oceanviewresort.com</a>.</p>
        </div>
        <div class="card">
          <h3>Phone</h3>
          <p>Call us at <a href="tel:+94911234567">+94 91 123 4567</a> for urgent assistance.</p>
        </div>
        <div class="card">
          <h3>Office Hours</h3>
          <p>Monday to Friday, 8:00 AM - 6:00 PM. Weekend support by email.</p>
        </div>
      </div>
    </section>

    <section class="section">
      <h2>Support Tips</h2>
      <div class="panel">
        <p>Include your username, role, and a short description of the issue for faster resolution.</p>
      </div>
    </section>
  </main>
  <%@ include file="/WEB-INF/partials/footer.jspf" %>
</body>
</html>

