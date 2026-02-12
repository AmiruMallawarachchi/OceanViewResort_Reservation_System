<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Login | OceanView Resort</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css?v=20260210" />
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
          <div class="password-input">
            <input type="password" name="password" id="loginPassword" placeholder="Enter password" class="<%= fieldErrors != null && fieldErrors.get("password") != null ? "input--error" : "" %>" required />
            <button type="button" class="password-toggle" id="toggleLoginPassword" aria-label="Show password" title="Show password">
              <span class="password-toggle__icon" aria-hidden="true">
                <svg width="18" height="18" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                  <path class="icon-eye__outline" d="M2.5 12C4.1 8.6 7.7 6 12 6s7.9 2.6 9.5 6c-1.6 3.4-5.2 6-9.5 6S4.1 15.4 2.5 12Z" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" />
                  <circle class="icon-eye__pupil" cx="12" cy="12" r="3" fill="currentColor" fill-opacity="0.65" />
                  <path class="icon-eye__slash" d="M5 5l14 14" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" />
                </svg>
              </span>
            </button>
          </div>
          <%
            if (fieldErrors != null && fieldErrors.get("password") != null) {
          %>
          <div class="form__error"><%= fieldErrors.get("password") %></div>
          <%
            }
          %>
        </div>
        <div class="form__row form__row--remember">
          <div class="form__row-left">
            <label class="form__checkbox-label">
              <input type="checkbox" name="rememberMe" value="on" />
              <span>Remember me</span>
            </label>
          </div>
          <a href="<%= request.getContextPath() %>/forgot-password.jsp" class="form__link muted">Forgot password?</a>
        </div>
        <button class="btn btn--primary" type="submit">Login</button>
        <% if (Boolean.TRUE.equals(request.getAttribute("googleLoginEnabled"))) { %>
        <div class="form__group" style="margin-top: 16px;">
          <div style="text-align: center; color: var(--muted); margin-bottom: 8px;">— or —</div>
          <a href="<%= request.getContextPath() %>/auth/google" class="btn btn--google" style="display: inline-flex; align-items: center; justify-content: center; gap: 8px; width: 100%; text-decoration: none;">
            <svg width="18" height="18" viewBox="0 0 18 18" xmlns="http://www.w3.org/2000/svg"><path fill="#4285F4" d="M17.64 9.2c0-.637-.057-1.251-.164-1.84H9v3.481h4.844c-.209 1.125-.843 2.078-1.796 2.717v2.258h2.908c1.702-1.567 2.684-3.874 2.684-6.615z"/><path fill="#34A853" d="M9 18c2.43 0 4.467-.806 6.168-2.183l-2.908-2.258c-.806.54-1.837.86-3.26.86-2.513 0-4.646-1.697-5.41-4.043H.957v2.331C2.438 15.983 5.482 18 9 18z"/><path fill="#FBBC05" d="M3.59 10.741c-.18-.54-.282-1.117-.282-1.741 0-.624.102-1.2.282-1.741V5.328H.957C.347 6.652 0 8.283 0 9.998s.348 3.346.957 4.67l2.633-2.006z"/><path fill="#EA4335" d="M9 3.58c1.321 0 2.508.454 3.44 1.345l2.582-2.58C13.463.891 11.426 0 9 0 5.482 0 2.438 2.017.957 5.328L3.59 7.334C4.354 4.99 6.487 3.293 9 3.293z"/></svg>
            Sign in with Google
          </a>
        </div>
        <% } %>
      </form>
      <p style="margin-top: 16px; color: var(--muted);">Need help? Visit the <a href="<%= request.getContextPath() %>/help.jsp">Help</a> page.</p>
    </div>
  </main>
  <%@ include file="/WEB-INF/partials/footer.jspf" %>
  <script>
    (function () {
      var passwordInput = document.getElementById("loginPassword");
      var passwordToggle = document.getElementById("toggleLoginPassword");
      if (passwordInput && passwordToggle) {
        passwordToggle.addEventListener("click", function () {
          var isPassword = passwordInput.type === "password";
          passwordInput.type = isPassword ? "text" : "password";
          passwordToggle.setAttribute("aria-label", isPassword ? "Hide password" : "Show password");
          passwordToggle.setAttribute("title", isPassword ? "Hide password" : "Show password");
          passwordToggle.classList.toggle("password-toggle--visible", !isPassword);
        });
      }
    })();
  </script>
</body>
</html>
