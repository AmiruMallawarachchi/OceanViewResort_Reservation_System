<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Forgot Password | OceanView Resort</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css?v=20260206" />
</head>
<body>
  <%@ include file="/WEB-INF/partials/nav.jspf" %>
  <main class="container">
    <div class="page-header">
      <div>
        <h1 class="page-header__title">Forgot Password</h1>
        <p class="muted">Enter your account email and we'll send you a reset code.</p>
      </div>
    </div>

    <%@ include file="/WEB-INF/partials/flash.jspf" %>
    <%@ include file="/WEB-INF/partials/field-errors.jspf" %>

    <div class="panel" style="max-width: 520px; margin: 0 auto;">
      <form class="form" method="post" action="<%= request.getContextPath() %>/auth">
        <input type="hidden" name="action" value="forgotPassword" />
        <div class="form__group">
          <label>Email address</label>
          <input type="email" name="email" placeholder="Enter your email" class="<%= fieldErrors != null && fieldErrors.get("email") != null ? "input--error" : "" %>" required />
          <%
            if (fieldErrors != null && fieldErrors.get("email") != null) {
          %>
          <div class="form__error"><%= fieldErrors.get("email") %></div>
          <%
            }
          %>
        </div>
        <button class="btn btn--primary" type="submit">Send reset code</button>
      </form>
      <p style="margin-top: 16px; color: var(--muted);">
        <a href="<%= request.getContextPath() %>/login.jsp">Back to login</a>
      </p>
    </div>
  </main>
  <%@ include file="/WEB-INF/partials/footer.jspf" %>
</body>
</html>
