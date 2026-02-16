<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List,com.oceanview.resort.dto.UserDTO,com.oceanview.resort.util.HtmlUtil" %>
<% String ctx = request.getContextPath(); %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Users | OceanView Resort</title>
  <link rel="stylesheet" href="<%= ctx %>/assets/css/style.css?v=20260213" />
</head>
<body>
  <%@ include file="/WEB-INF/partials/nav.jspf" %>
  <main class="container">
    <%
      UserDTO editUser = (UserDTO) request.getAttribute("editUser");
      boolean isEdit = editUser != null;
      String searchQuery = request.getParameter("q");
      String roleFilter = request.getParameter("role");
      String statusFilter = request.getParameter("status");
      List<UserDTO> users = (List<UserDTO>) request.getAttribute("users");
      int userCount = users == null ? 0 : users.size();
    %>

    <div class="page-header page-header--row">
      <div>
        <h1 class="page-header__title">Users</h1>
        <p class="muted"><%= userCount %> team members</p>
      </div>
      <a class="btn btn--primary" href="<%= ctx %>/users#user-form">Add User</a>
    </div>

    <%@ include file="/WEB-INF/partials/flash.jspf" %>
    <%@ include file="/WEB-INF/partials/field-errors.jspf" %>

    <form class="filters" method="get" action="<%= ctx %>/users" style="margin-bottom: 20px;">
      <div class="filters__group filters__group--search">
        <input type="text" name="q" placeholder="Search by name or email..." value="<%= searchQuery == null ? "" : HtmlUtil.escape(searchQuery) %>" />
      </div>
      <input type="hidden" name="role" value="<%= roleFilter == null ? "" : roleFilter %>" />
      <input type="hidden" name="status" value="<%= statusFilter == null ? "" : statusFilter %>" />
      <button type="submit" class="btn btn--outline btn--sm">Search</button>
      <div class="role-tabs" style="flex: 1; display: flex; flex-wrap: wrap; gap: 8px; align-items: center;">
        <a href="<%= ctx %>/users?q=<%= searchQuery != null ? java.net.URLEncoder.encode(searchQuery, "UTF-8") : "" %>" class="pill <%= (roleFilter == null || roleFilter.isEmpty()) ? "active" : "" %>" style="padding: 8px 16px; text-decoration: none; border-radius: 10px; background: <%= (roleFilter == null || roleFilter.isEmpty()) ? "var(--brand)" : "var(--card)" %>; color: <%= (roleFilter == null || roleFilter.isEmpty()) ? "#fff" : "var(--text)" %>; border: 1px solid var(--border);">All</a>
        <a href="<%= ctx %>/users?role=RESERVATIONIST&q=<%= searchQuery != null ? java.net.URLEncoder.encode(searchQuery, "UTF-8") : "" %>" class="pill <%= "RESERVATIONIST".equalsIgnoreCase(roleFilter) ? "active" : "" %>" style="padding: 8px 16px; text-decoration: none; border-radius: 10px; background: <%= "RESERVATIONIST".equalsIgnoreCase(roleFilter) ? "var(--brand)" : "var(--card)" %>; color: <%= "RESERVATIONIST".equalsIgnoreCase(roleFilter) ? "#fff" : "var(--text)" %>; border: 1px solid var(--border);">Reservationist</a>
        <a href="<%= ctx %>/users?role=ADMIN&q=<%= searchQuery != null ? java.net.URLEncoder.encode(searchQuery, "UTF-8") : "" %>" class="pill <%= "ADMIN".equalsIgnoreCase(roleFilter) ? "active" : "" %>" style="padding: 8px 16px; text-decoration: none; border-radius: 10px; background: <%= "ADMIN".equalsIgnoreCase(roleFilter) ? "var(--brand)" : "var(--card)" %>; color: <%= "ADMIN".equalsIgnoreCase(roleFilter) ? "#fff" : "var(--text)" %>; border: 1px solid var(--border);">Admin</a>
      </div>
    </form>

    <div class="panel">
      <table class="table table--striped">
        <thead>
          <tr>
            <th>USER</th>
            <th>ROLE</th>
            <th>STATUS</th>
            <th>LAST LOGIN</th>
            <th>ACTIONS</th>
          </tr>
        </thead>
        <tbody>
          <%
            if (users != null && !users.isEmpty()) {
              for (UserDTO user : users) {
                String initials = "";
                if (user.getFullName() != null && !user.getFullName().isBlank()) {
                  String[] parts = user.getFullName().trim().split("\\s+");
                  if (parts.length >= 2) initials = (parts[0].substring(0,1) + parts[parts.length-1].substring(0,1)).toUpperCase();
                  else if (parts[0].length() >= 1) initials = parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
                }
                if (initials.isEmpty() && user.getUsername() != null && user.getUsername().length() >= 1) initials = user.getUsername().substring(0, Math.min(2, user.getUsername().length())).toUpperCase();
          %>
          <tr>
            <td>
              <div class="user-cell">
                <span class="user-avatar"><%= initials %></span>
                <div>
                  <div class="user-cell__name"><%= HtmlUtil.escape(user.getFullName() != null ? user.getFullName() : user.getUsername()) %></div>
                  <div class="user-cell__email"><%= user.getEmail() != null ? HtmlUtil.escape(user.getEmail()) : "—" %></div>
                </div>
              </div>
            </td>
            <td><span class="pill pill--role"><%= user.getRole() != null ? user.getRole() : "—" %></span></td>
            <td><span class="pill <%= user.isActive() ? "pill--active" : "pill--inactive" %>"><%= user.isActive() ? "Active" : "Inactive" %></span></td>
            <td>—</td>
            <td>
              <div class="table-actions">
                <a class="btn btn--outline btn--sm" href="<%= ctx %>/users?editId=<%= user.getId() %>">Edit</a>
                <% if (user.getEmail() != null && !user.getEmail().isBlank()) { %>
                <form method="post" action="<%= ctx %>/users" onsubmit="return confirm('Send password reset email to this user?');" style="display:inline;">
                  <input type="hidden" name="action" value="sendResetPassword" />
                  <input type="hidden" name="id" value="<%= user.getId() %>" />
                  <button class="btn btn--outline btn--sm" type="submit">Reset password</button>
                </form>
                <% } %>
                <form method="post" action="<%= ctx %>/users" onsubmit="return confirm('Delete this user?');" style="display:inline;">
                  <input type="hidden" name="action" value="delete" />
                  <input type="hidden" name="id" value="<%= user.getId() %>" />
                  <button class="btn btn--outline btn--sm" type="submit">Delete</button>
                </form>
              </div>
            </td>
          </tr>
          <%
              }
            } else {
          %>
          <tr>
            <td colspan="5">No users found.</td>
          </tr>
          <% } %>
        </tbody>
      </table>
    </div>

    <div class="panel" id="user-form" style="margin-top: 24px;">
      <h2><%= isEdit ? "Edit User" : "Create User" %></h2>
        <form class="form" method="post" action="<%= ctx %>/users">
          <input type="hidden" name="action" value="<%= isEdit ? "update" : "create" %>" />
          <%
            if (isEdit) {
          %>
          <input type="hidden" name="id" value="<%= editUser.getId() %>" />
          <%
            }
          %>
          <div class="form__group">
            <label>Full Name</label>
            <input type="text" name="fullName" placeholder="Full name" value="<%= isEdit ? HtmlUtil.escape(editUser.getFullName()) : "" %>" pattern="[A-Za-z ]+" title="Letters and spaces only" class="<%= fieldErrors != null && fieldErrors.get("fullName") != null ? "input--error" : "" %>" required />
            <%
              if (fieldErrors != null && fieldErrors.get("fullName") != null) {
            %>
            <div class="form__error"><%= HtmlUtil.escape(fieldErrors.get("fullName")) %></div>
            <%
              }
            %>
          </div>
          <div class="form__group">
            <label>Username</label>
            <input type="text" id="<%= isEdit ? "editUsername" : "createUsername" %>" name="username" placeholder="Username" value="<%= isEdit ? HtmlUtil.escape(editUser.getUsername()) : "" %>" class="<%= fieldErrors != null && fieldErrors.get("username") != null ? "input--error" : "" %>" required />
            <%
              if (fieldErrors != null && fieldErrors.get("username") != null) {
            %>
            <div class="form__error"><%= HtmlUtil.escape(fieldErrors.get("username")) %></div>
            <%
              }
            %>
            <%
              if (!isEdit) {
            %>
            <div class="form__hint" id="usernameHint">Username must be unique.</div>
            <%
              }
            %>
          </div>
          <div class="form__group">
            <label>Email</label>
            <input type="email" name="email" placeholder="Email" value="<%= isEdit ? (editUser.getEmail() == null ? "" : HtmlUtil.escape(editUser.getEmail())) : "" %>" />
            <%
              if (fieldErrors != null && fieldErrors.get("email") != null) {
            %>
            <div class="form__error"><%= HtmlUtil.escape(fieldErrors.get("email")) %></div>
            <%
              }
            %>
          </div>
          <%
            if (!isEdit) {
          %>
          <div class="form__group">
            <label>Password</label>
            <div class="password-input">
              <input
                  type="password"
                  id="createPassword"
                  name="password"
                  placeholder="Temporary password"
                  autocomplete="new-password"
                  minlength="8"
                  class="<%= fieldErrors != null && fieldErrors.get("password") != null ? "input--error" : "" %>"
                  required />
              <button type="button" class="password-toggle" id="toggleCreatePassword" aria-label="Show password">
                <span class="password-toggle__icon" aria-hidden="true">
                  <svg width="18" height="18" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                    <!-- Eye outline -->
                    <path class="icon-eye__outline"
                          d="M2.5 12C4.1 8.6 7.7 6 12 6s7.9 2.6 9.5 6c-1.6 3.4-5.2 6-9.5 6S4.1 15.4 2.5 12Z"
                          fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"
                          stroke-linejoin="round" />
                    <!-- Pupil -->
                    <circle class="icon-eye__pupil" cx="12" cy="12" r="3"
                            fill="currentColor" fill-opacity="0.65" />
                    <!-- Slash (shown when visible) -->
                    <path class="icon-eye__slash"
                          d="M5 5l14 14"
                          fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"
                          stroke-linejoin="round" />
                  </svg>
                </span>
              </button>
            </div>
            <%
              if (fieldErrors != null && fieldErrors.get("password") != null) {
            %>
            <div class="form__error"><%= HtmlUtil.escape(fieldErrors.get("password")) %></div>
            <%
              }
            %>
            <div class="password-strength" id="passwordStrength" data-strength="weak">
              <div class="password-strength__bar">
                <span class="password-strength__fill" id="passwordStrengthFill"></span>
              </div>
              <div class="password-strength__text" id="passwordStrengthText" aria-live="polite">Strength: Weak</div>
              <ul class="password-requirements" id="passwordRequirements">
                <li data-rule="length">At least 8 characters</li>
                <li data-rule="lower">At least 1 lowercase letter</li>
                <li data-rule="upper">At least 1 uppercase letter</li>
                <li data-rule="number">At least 1 number</li>
                <li data-rule="special">At least 1 special character</li>
              </ul>
            </div>
          </div>
          <%
            }
          %>
          <div class="form__group">
            <label>Role</label>
            <select name="role">
              <option value="RESERVATIONIST" <%= isEdit && "RESERVATIONIST".equals(editUser.getRole()) ? "selected" : "" %>>Reservationist</option>
              <option value="ADMIN" <%= isEdit && "ADMIN".equals(editUser.getRole()) ? "selected" : "" %>>Administrator</option>
            </select>
            <%
              if (fieldErrors != null && fieldErrors.get("role") != null) {
            %>
            <div class="form__error"><%= HtmlUtil.escape(fieldErrors.get("role")) %></div>
            <%
              }
            %>
          </div>
          <%
            if (isEdit) {
          %>
          <div class="form__group">
            <label>Status</label>
            <select name="active">
              <option value="true" <%= editUser.isActive() ? "selected" : "" %>>Active</option>
              <option value="false" <%= !editUser.isActive() ? "selected" : "" %>>Inactive</option>
            </select>
          </div>
          <%
            }
          %>
          <button class="btn btn--primary" type="submit"><%= isEdit ? "Update User" : "Create User" %></button>
          <%
            if (isEdit) {
          %>
          <a class="btn btn--outline" href="<%= ctx %>/users">Cancel</a>
          <%
            }
          %>
        </form>
    </div>
  </main>
  <%@ include file="/WEB-INF/partials/footer.jspf" %>
  <%
    if (!isEdit) {
      List<UserDTO> usersForHints = (List<UserDTO>) request.getAttribute("users");
  %>
  <script>
    document.addEventListener("DOMContentLoaded", function () {
      var passwordInput = document.getElementById("createPassword");
      var passwordToggle = document.getElementById("toggleCreatePassword");
      var strengthText = document.getElementById("passwordStrengthText");
      var strengthFill = document.getElementById("passwordStrengthFill");
      var strengthWrap = document.getElementById("passwordStrength");
      var requirements = document.getElementById("passwordRequirements");
      var usernameInput = document.getElementById("createUsername");
      var usernameHint = document.getElementById("usernameHint");
      var existingUsernames = [
        <%
          if (usersForHints != null) {
            for (UserDTO user : usersForHints) {
              String usernameForHint = user.getUsername();
              if (usernameForHint != null) {
        %>
        "<%= usernameForHint.replace("\\\\", "\\\\\\\\").replace("\"", "\\\\\"") %>",
        <%
              }
            }
          }
        %>
      ].map(function (value) { return value.toLowerCase(); });

      function updateUsernameHint() {
        if (!usernameInput || !usernameHint) {
          return;
        }
        var value = usernameInput.value.trim().toLowerCase();
        usernameHint.className = "form__hint";
        if (!value) {
          usernameHint.textContent = "Username must be unique.";
          return;
        }
        if (existingUsernames.indexOf(value) !== -1) {
          usernameHint.textContent = "Username already exists.";
          usernameHint.className = "form__hint form__hint--error";
        } else {
          usernameHint.textContent = "Username is available.";
          usernameHint.className = "form__hint form__hint--success";
        }
      }

      function updatePasswordStrength() {
        if (!passwordInput || !strengthText || !strengthFill || !strengthWrap || !requirements) {
          return;
        }
        var value = passwordInput.value || "";
        var checks = {
          length: value.length >= 8,
          lower: /[a-z]/.test(value),
          upper: /[A-Z]/.test(value),
          // Use explicit [0-9] character class for robustness
          number: /[0-9]/.test(value),
          special: /[^A-Za-z0-9]/.test(value)
        };

        var passed = 0;
        Object.keys(checks).forEach(function (key) {
          if (checks[key]) {
            passed += 1;
          }
          var item = requirements.querySelector('[data-rule="' + key + '"]');
          if (item) {
            item.classList.toggle("ok", checks[key]);
          }
        });

        var strength = "Weak";
        if (passed >= 5) {
          strength = "Strong";
        } else if (passed >= 3) {
          strength = "Good";
        }
        strengthWrap.setAttribute("data-strength", strength.toLowerCase());
        strengthText.textContent = "Strength: " + strength;
        strengthFill.style.width = Math.round((passed / 5) * 100) + "%";
      }

      if (usernameInput) {
        usernameInput.addEventListener("input", updateUsernameHint);
        updateUsernameHint();
      }
      if (passwordInput) {
        passwordInput.addEventListener("input", updatePasswordStrength);
        updatePasswordStrength();
      }

      if (passwordInput && passwordToggle) {
        passwordToggle.addEventListener("click", function () {
          var isPassword = passwordInput.type === "password";
          passwordInput.type = isPassword ? "text" : "password";
          passwordToggle.setAttribute("aria-label", isPassword ? "Hide password" : "Show password");
          passwordToggle.classList.toggle("password-toggle--visible", !isPassword);
        });
      }
    });
  </script>
  <%
    }
  %>
</body>
</html>

