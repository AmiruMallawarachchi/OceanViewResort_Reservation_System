<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List,com.oceanview.resort.dto.UserDTO" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Manage Users</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css?v=20260206" />
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

    <%@ include file="/WEB-INF/partials/flash.jspf" %>
    <%@ include file="/WEB-INF/partials/field-errors.jspf" %>

    <%
      UserDTO editUser = (UserDTO) request.getAttribute("editUser");
      boolean isEdit = editUser != null;
    String searchQuery = request.getParameter("q");
    String roleFilter = request.getParameter("role");
    String statusFilter = request.getParameter("status");
    %>

    <div class="grid" style="grid-template-columns: 1fr 1fr; gap: 20px;">
      <div class="panel">
        <h2><%= isEdit ? "Edit User" : "Create User" %></h2>
        <form class="form" method="post" action="<%= request.getContextPath() %>/users">
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
            <input type="text" name="fullName" placeholder="Full name" value="<%= isEdit ? editUser.getFullName() : "" %>" pattern="[A-Za-z ]+" title="Letters and spaces only" class="<%= fieldErrors != null && fieldErrors.get("fullName") != null ? "input--error" : "" %>" required />
            <%
              if (fieldErrors != null && fieldErrors.get("fullName") != null) {
            %>
            <div class="form__error"><%= fieldErrors.get("fullName") %></div>
            <%
              }
            %>
          </div>
          <div class="form__group">
            <label>Username</label>
            <input type="text" id="<%= isEdit ? "editUsername" : "createUsername" %>" name="username" placeholder="Username" value="<%= isEdit ? editUser.getUsername() : "" %>" class="<%= fieldErrors != null && fieldErrors.get("username") != null ? "input--error" : "" %>" required />
            <%
              if (fieldErrors != null && fieldErrors.get("username") != null) {
            %>
            <div class="form__error"><%= fieldErrors.get("username") %></div>
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
            <input type="email" name="email" placeholder="Email" value="<%= isEdit ? (editUser.getEmail() == null ? "" : editUser.getEmail()) : "" %>" />
            <%
              if (fieldErrors != null && fieldErrors.get("email") != null) {
            %>
            <div class="form__error"><%= fieldErrors.get("email") %></div>
            <%
              }
            %>
          </div>
          <%
            if (!isEdit) {
          %>
          <div class="form__group">
            <label>Password</label>
            <input type="password" id="createPassword" name="password" placeholder="Temporary password" autocomplete="new-password" minlength="8" class="<%= fieldErrors != null && fieldErrors.get("password") != null ? "input--error" : "" %>" required />
            <%
              if (fieldErrors != null && fieldErrors.get("password") != null) {
            %>
            <div class="form__error"><%= fieldErrors.get("password") %></div>
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
            <div class="form__error"><%= fieldErrors.get("role") %></div>
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
          <a class="btn btn--outline" href="<%= request.getContextPath() %>/users">Cancel</a>
          <%
            }
          %>
        </form>
      </div>

      <div class="panel">
        <h2>Active Users</h2>
        <div class="table-filters">
          <form class="filters" method="get" action="<%= request.getContextPath() %>/users">
            <div class="filters__group filters__group--search">
              <input type="text" name="q" placeholder="Search name, username, email" value="<%= searchQuery == null ? "" : searchQuery %>" />
            </div>
            <div class="filters__group">
              <select name="role">
                <option value="">All Roles</option>
                <option value="ADMIN" <%= "ADMIN".equalsIgnoreCase(roleFilter) ? "selected" : "" %>>Admin</option>
                <option value="RESERVATIONIST" <%= "RESERVATIONIST".equalsIgnoreCase(roleFilter) ? "selected" : "" %>>Reservationist</option>
              </select>
            </div>
            <div class="filters__group">
              <select name="status">
                <option value="">All Status</option>
                <option value="ACTIVE" <%= "ACTIVE".equalsIgnoreCase(statusFilter) ? "selected" : "" %>>Active</option>
                <option value="INACTIVE" <%= "INACTIVE".equalsIgnoreCase(statusFilter) ? "selected" : "" %>>Inactive</option>
              </select>
            </div>
            <button class="btn btn--outline btn--sm" type="submit">Filter</button>
            <a class="btn btn--outline btn--sm" href="<%= request.getContextPath() %>/users">Clear</a>
          </form>
        </div>
        <table class="table">
          <thead>
            <tr>
              <th>Name</th>
              <th>Role</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <%
              List<UserDTO> users = (List<UserDTO>) request.getAttribute("users");
              if (users != null && !users.isEmpty()) {
                for (UserDTO user : users) {
            %>
            <tr>
              <td><%= user.getFullName() %></td>
              <td><%= user.getRole() %></td>
              <td><%= user.isActive() ? "Active" : "Inactive" %></td>
              <td>
                <div class="table-actions">
                  <a class="btn btn--outline btn--sm" href="<%= request.getContextPath() %>/users?editId=<%= user.getId() %>">Edit</a>
                  <form method="post" action="<%= request.getContextPath() %>/users" onsubmit="return confirm('Delete this user?');">
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
              <td colspan="4">No users found.</td>
            </tr>
            <%
              }
            %>
          </tbody>
        </table>
      </div>
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
              String username = user.getUsername();
              if (username != null) {
        %>
        "<%= username.replace("\\\\", "\\\\\\\\").replace("\"", "\\\\\"") %>",
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
          number: /\\d/.test(value),
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
    });
  </script>
  <%
    }
  %>
</body>
</html>

