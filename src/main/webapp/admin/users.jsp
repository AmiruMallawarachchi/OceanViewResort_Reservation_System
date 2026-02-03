<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Manage Users</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css" />
</head>
<body>
  <%@ include file="/WEB-INF/partials/nav.jspf" %>
  <main class="container">
    <div class="page-header">
      <div>
        <h1 class="page-header__title">User Management</h1>
        <p class="muted">Create, update, and deactivate system users.</p>
      </div>
    </div>

    <div class="grid" style="grid-template-columns: 1fr 1fr; gap: 20px;">
      <div class="panel">
        <h2>Create User</h2>
        <form class="form">
          <div class="form__group">
            <label>Full Name</label>
            <input type="text" placeholder="Full name" />
          </div>
          <div class="form__group">
            <label>Username</label>
            <input type="text" placeholder="Username" />
          </div>
          <div class="form__group">
            <label>Role</label>
            <select>
              <option>Reservationist</option>
              <option>Administrator</option>
            </select>
          </div>
          <button class="btn btn--primary" type="submit">Create User</button>
        </form>
      </div>

      <div class="panel">
        <h2>Active Users</h2>
        <table class="table">
          <thead>
            <tr>
              <th>Name</th>
              <th>Role</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>Admin User</td>
              <td>Administrator</td>
              <td>Active</td>
            </tr>
            <tr>
              <td>Res. Staff</td>
              <td>Reservationist</td>
              <td>Active</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </main>
  <%@ include file="/WEB-INF/partials/footer.jspf" %>
</body>
</html>
