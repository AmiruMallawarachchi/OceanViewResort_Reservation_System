<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>About | OceanView Resort</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css" />
</head>
<body>
  <%@ include file="/WEB-INF/partials/nav.jspf" %>
  <main class="container">
    <div class="page-header">
      <div>
        <h1 class="page-header__title">About OceanView Resort</h1>
        <p class="muted">A luxury beachfront experience backed by smart technology.</p>
      </div>
      <span class="badge">Est. 1998</span>
    </div>

    <div class="panel">
      <p>OceanView Resort is located in the heart of Galle, offering premium rooms, tropical dining, and personalized guest services. Our reservation system enables staff to manage bookings efficiently, ensuring guests always receive accurate information and smooth check-ins.</p>
      <div class="section">
        <h2>Our Mission</h2>
        <p>Deliver seamless hospitality through innovation, transparency, and guest-first service.</p>
      </div>
      <div class="section">
        <h2>Our Values</h2>
        <ul>
          <li>Integrity in every booking</li>
          <li>Reliability across every shift</li>
          <li>Excellence in guest satisfaction</li>
        </ul>
      </div>
    </div>
  </main>
  <%@ include file="/WEB-INF/partials/footer.jspf" %>
</body>
</html>
