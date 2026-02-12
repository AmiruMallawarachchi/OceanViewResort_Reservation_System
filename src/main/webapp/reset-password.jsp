<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String resetEmail = session != null ? (String) session.getAttribute("resetEmail") : null;
  if (resetEmail == null || resetEmail.isBlank()) {
    resetEmail = "";
  }
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Reset Password | OceanView Resort</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css?v=20260206" />
</head>
<body>
  <%@ include file="/WEB-INF/partials/nav.jspf" %>
  <main class="container">
    <div class="page-header">
      <div>
        <h1 class="page-header__title">Reset Password</h1>
        <p class="muted">Enter the code we sent to your email and choose a new password.</p>
      </div>
    </div>

    <%@ include file="/WEB-INF/partials/flash.jspf" %>
    <%@ include file="/WEB-INF/partials/field-errors.jspf" %>

    <div class="panel" style="max-width: 520px; margin: 0 auto;">
      <form class="form" method="post" action="<%= request.getContextPath() %>/auth">
        <input type="hidden" name="action" value="resetPassword" />
        <% if (resetEmail != null && !resetEmail.isEmpty()) { %>
        <input type="hidden" name="email" value="<%= resetEmail %>" />
        <% } else { %>
        <div class="form__group">
          <label>Email address</label>
          <input type="email" name="email" placeholder="Your email" required />
        </div>
        <% } %>
        <div class="form__group">
          <label>Reset code</label>
          <input type="text" name="otp" placeholder="Enter 6-digit code" maxlength="6" autocomplete="one-time-code" class="<%= fieldErrors != null && fieldErrors.get("otp") != null ? "input--error" : "" %>" required />
          <%
            if (fieldErrors != null && fieldErrors.get("otp") != null) {
          %>
          <div class="form__error"><%= fieldErrors.get("otp") %></div>
          <%
            }
          %>
        </div>
        <div class="form__group">
          <label>New password</label>
          <input type="password" name="newPassword" placeholder="At least 6 characters" class="<%= fieldErrors != null && fieldErrors.get("newPassword") != null ? "input--error" : "" %>" required minlength="6" />
          <%
            if (fieldErrors != null && fieldErrors.get("newPassword") != null) {
          %>
          <div class="form__error"><%= fieldErrors.get("newPassword") %></div>
          <%
            }
          %>
        </div>
        <div class="form__group">
          <label>Confirm new password</label>
          <input type="password" name="confirmPassword" placeholder="Re-enter password" class="<%= fieldErrors != null && fieldErrors.get("confirmPassword") != null ? "input--error" : "" %>" required minlength="6" />
          <%
            if (fieldErrors != null && fieldErrors.get("confirmPassword") != null) {
          %>
          <div class="form__error"><%= fieldErrors.get("confirmPassword") %></div>
          <%
            }
          %>
        </div>
        <button class="btn btn--primary" type="submit">Update password</button>
      </form>
      <p style="margin-top: 16px; color: var(--muted);">
        <a href="<%= request.getContextPath() %>/forgot-password.jsp">Request a new code</a> &middot;
        <a href="<%= request.getContextPath() %>/login.jsp">Back to login</a>
      </p>
    </div>
  </main>
  <%@ include file="/WEB-INF/partials/footer.jspf" %>
</body>
</html>
