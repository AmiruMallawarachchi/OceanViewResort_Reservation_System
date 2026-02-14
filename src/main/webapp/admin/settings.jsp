<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% String ctx = request.getContextPath(); %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Settings | OceanView Resort</title>
  <link rel="stylesheet" href="<%= ctx %>/assets/css/style.css?v=20260213" />
</head>
<body>
  <%@ include file="/WEB-INF/partials/nav.jspf" %>
  <main class="container">
    <div class="page-header">
      <div>
        <h1 class="page-header__title">Settings</h1>
        <p class="muted">Tax rates and system configuration.</p>
      </div>
    </div>

    <%@ include file="/WEB-INF/partials/flash.jspf" %>

    <div class="panel">
      <h2>Tax rate</h2>
      <form class="form" method="post" action="<%= ctx %>/settings">
        <div class="form__group">
          <label for="taxRatePercent">Default tax rate (%)</label>
          <input type="number" id="taxRatePercent" name="taxRatePercent" min="0" max="100" step="0.01" value="<%= request.getAttribute("taxRatePercent") != null ? request.getAttribute("taxRatePercent") : "10" %>" required />
          <div class="form__hint">Applied to room charges when generating bills.</div>
        </div>
        <button class="btn btn--primary" type="submit">Save</button>
      </form>
    </div>

    <div class="panel">
      <h2>Email templates</h2>
      <p class="muted">Reservation confirmation and cancellation emails are sent using templates defined in the system.</p>
      <ul style="margin: 12px 0; padding-left: 20px;">
        <li><strong>Confirmation</strong> – sent when a new reservation is created</li>
        <li><strong>Cancellation</strong> – sent when a reservation is cancelled</li>
      </ul>
      <a class="btn btn--outline" href="<%= ctx %>/admin/email-templates.jsp">View email templates</a>
    </div>
  </main>
  <%@ include file="/WEB-INF/partials/footer.jspf" %>
</body>
</html>
