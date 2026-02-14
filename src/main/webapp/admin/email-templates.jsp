<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% String ctx = request.getContextPath(); %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Email Templates | OceanView Resort</title>
  <link rel="stylesheet" href="<%= ctx %>/assets/css/style.css?v=20260213" />
</head>
<body>
  <%@ include file="/WEB-INF/partials/nav.jspf" %>
  <main class="container">
    <div class="page-header">
      <div>
        <h1 class="page-header__title">Email templates</h1>
        <p class="muted">Templates used for reservation confirmation and cancellation emails.</p>
      </div>
      <a class="btn btn--outline" href="<%= ctx %>/settings">Back to Settings</a>
    </div>

    <div class="panel">
      <h2>Confirmation</h2>
      <p class="muted">Sent when a new reservation is created. Contains reservation number, dates, and guest details.</p>
    </div>
    <div class="panel">
      <h2>Cancellation</h2>
      <p class="muted">Sent when a reservation is cancelled. Contains reservation number and cancellation notice.</p>
    </div>
    <p class="muted" style="margin-top: 16px;">Templates are implemented in <code>EmailTemplateUtil.java</code>. To customize subject or body, edit the build methods in that class.</p>
  </main>
  <%@ include file="/WEB-INF/partials/footer.jspf" %>
</body>
</html>
