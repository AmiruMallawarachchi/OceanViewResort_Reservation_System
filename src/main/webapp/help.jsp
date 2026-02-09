<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Help | OceanView Resort</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css?v=20260206" />
</head>
<body>
  <%@ include file="/WEB-INF/partials/nav.jspf" %>
  <main class="container">
    <div class="page-header">
      <div>
        <h1 class="page-header__title">Help & Support</h1>
        <p class="muted">Quick guidance for common tasks and questions.</p>
      </div>
      <span class="badge">Support</span>
    </div>

    <section class="section">
      <h2>Quick Help</h2>
      <div class="grid grid--3">
        <div class="card">
          <h3>Login Issues</h3>
          <p>If you cannot sign in, verify your username and password or contact an administrator for a reset.</p>
        </div>
        <div class="card">
          <h3>New Reservation</h3>
          <p>Use the Reservations screen to check availability, then create a guest profile and confirm dates.</p>
        </div>
        <div class="card">
          <h3>Billing Questions</h3>
          <p>Review nightly rates and taxes in the Billing screen before finalizing checkout.</p>
        </div>
      </div>
    </section>

    <section class="section">
      <h2>Frequently Asked Questions</h2>
      <div class="panel">
        <p><strong>How do I access the dashboard?</strong><br />Log in with your assigned role to see the appropriate dashboard.</p>
        <p><strong>How do I update a reservation?</strong><br />Open the reservation, adjust dates or room type, then save changes.</p>
        <p><strong>Who can manage rooms and users?</strong><br />Only administrators can access room, user, and report management.</p>
      </div>
    </section>
  </main>
  <%@ include file="/WEB-INF/partials/footer.jspf" %>
</body>
</html>

