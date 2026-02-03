<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Help | OceanView Resort</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css" />
</head>
<body>
  <%@ include file="/WEB-INF/partials/nav.jspf" %>
  <main class="container">
    <div class="page-header">
      <div>
        <h1 class="page-header__title">Help & User Guide</h1>
        <p class="muted">Quick steps to use the reservation system.</p>
      </div>
    </div>

    <div class="grid grid--3">
      <div class="card">
        <h3>1. Login</h3>
        <p>Use your assigned username and password. Your role determines your access.</p>
      </div>
      <div class="card">
        <h3>2. Add Reservation</h3>
        <p>Enter guest details, select room type, and confirm dates.</p>
      </div>
      <div class="card">
        <h3>3. Generate Bill</h3>
        <p>Calculate the stay cost and print the final bill.</p>
      </div>
    </div>

    <div class="section panel">
      <h2>Common Tasks</h2>
      <ul>
        <li>Search reservations by ID or guest name</li>
        <li>Update reservation dates and rooms</li>
        <li>Cancel reservations and notify guests</li>
        <li>Generate management reports</li>
      </ul>
    </div>
  </main>
  <%@ include file="/WEB-INF/partials/footer.jspf" %>
</body>
</html>
