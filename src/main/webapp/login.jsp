<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Login | OceanView Resort</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css" />
</head>
<body>
  <%@ include file="/WEB-INF/partials/nav.jspf" %>
  <main class="container">
    <div class="page-header">
      <div>
        <h1 class="page-header__title">System Login</h1>
        <p class="muted">Sign in to access reservation tools.</p>
      </div>
    </div>

    <div class="panel" style="max-width: 520px; margin: 0 auto;">
      <form class="form">
        <div class="form__group">
          <label>Username</label>
          <input type="text" placeholder="Enter username" />
        </div>
        <div class="form__group">
          <label>Password</label>
          <input type="password" placeholder="Enter password" />
        </div>
        <div class="form__group">
          <label>Role</label>
          <select>
            <option>Reservationist</option>
            <option>Administrator</option>
          </select>
        </div>
        <button class="btn btn--primary" type="submit">Login</button>
      </form>
      <p style="margin-top: 16px; color: var(--muted);">Need help? Visit the <a href="<%= request.getContextPath() %>/help.jsp">Help</a> page.</p>
    </div>
  </main>
  <%@ include file="/WEB-INF/partials/footer.jspf" %>
</body>
</html>
