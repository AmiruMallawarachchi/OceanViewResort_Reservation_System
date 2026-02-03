<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Admin Dashboard</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css" />
</head>
<body>
  <%@ include file="/WEB-INF/partials/nav.jspf" %>
  <main class="container">
    <div class="page-header">
      <div>
        <h1 class="page-header__title">Administrator Dashboard</h1>
        <p class="muted">Manage users, rooms, and reports.</p>
      </div>
      <span class="badge">Admin Access</span>
    </div>

    <div class="grid grid--3">
      <div class="card">
        <h3>Manage Users</h3>
        <p>Create and manage staff accounts.</p>
        <a class="btn btn--primary" href="<%= request.getContextPath() %>/admin/users.jsp">Open</a>
      </div>
      <div class="card">
        <h3>Rooms & Types</h3>
        <p>Update room inventory and room types.</p>
        <a class="btn btn--outline" href="<%= request.getContextPath() %>/admin/rooms.jsp">Manage</a>
      </div>
      <div class="card">
        <h3>Reports</h3>
        <p>Generate occupancy and revenue reports.</p>
        <a class="btn btn--outline" href="<%= request.getContextPath() %>/admin/reports.jsp">View</a>
      </div>
    </div>
  </main>
  <%@ include file="/WEB-INF/partials/footer.jspf" %>
</body>
</html>
