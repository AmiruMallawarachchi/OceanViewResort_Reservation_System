<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Login | OceanView Resort</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css?v=20260206" />
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

    <%@ include file="/WEB-INF/partials/flash.jspf" %>
    <%@ include file="/WEB-INF/partials/field-errors.jspf" %>

    <div class="panel" style="max-width: 520px; margin: 0 auto;">
      <form class="form" method="post" action="<%= request.getContextPath() %>/auth">
        <input type="hidden" name="action" value="login" />
        <div class="form__group">
          <label>Username</label>
          <input type="text" name="username" placeholder="Enter username" class="<%= fieldErrors != null && fieldErrors.get("username") != null ? "input--error" : "" %>" required />
          <%
            if (fieldErrors != null && fieldErrors.get("username") != null) {
          %>
          <div class="form__error"><%= fieldErrors.get("username") %></div>
          <%
            }
          %>
        </div>
        <div class="form__group">
          <label>Password</label>
          <input type="password" name="password" placeholder="Enter password" class="<%= fieldErrors != null && fieldErrors.get("password") != null ? "input--error" : "" %>" required />
          <%
            if (fieldErrors != null && fieldErrors.get("password") != null) {
          %>
          <div class="form__error"><%= fieldErrors.get("password") %></div>
          <%
            }
          %>
        </div>
        <button class="btn btn--primary" type="submit">Login</button>
      </form>
      <p style="margin-top: 16px; color: var(--muted);">Need help? Visit the <a href="<%= request.getContextPath() %>/help.jsp">Help</a> page.</p>
    </div>
  </main>
  <%@ include file="/WEB-INF/partials/footer.jspf" %>
</body>
</html>
