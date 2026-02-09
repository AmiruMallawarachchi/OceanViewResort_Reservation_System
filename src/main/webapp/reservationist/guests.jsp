<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List,com.oceanview.resort.dto.GuestDTO" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Guest Management</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css?v=20260206" />
</head>
<body>
  <%@ include file="/WEB-INF/partials/nav.jspf" %>
  <main class="container">
    <div class="page-header">
      <div>
        <h1 class="page-header__title">Guest Management</h1>
        <p class="muted">Register and maintain guest information.</p>
      </div>
    </div>

    <%@ include file="/WEB-INF/partials/flash.jspf" %>
    <%@ include file="/WEB-INF/partials/field-errors.jspf" %>

    <%
      GuestDTO editGuest = (GuestDTO) request.getAttribute("editGuest");
      boolean isEdit = editGuest != null;
    %>

    <div class="grid" style="grid-template-columns: 1fr 1fr; gap: 20px;">
      <div class="panel">
        <h2><%= isEdit ? "Edit Guest" : "Register Guest" %></h2>
        <form class="form" method="post" action="<%= request.getContextPath() %>/guests">
          <input type="hidden" name="action" value="<%= isEdit ? "update" : "create" %>" />
          <%
            if (isEdit) {
          %>
          <input type="hidden" name="id" value="<%= editGuest.getId() %>" />
          <%
            }
          %>
          <div class="form__group">
            <label>Full Name</label>
            <input type="text" name="fullName" placeholder="Guest full name" value="<%= isEdit ? editGuest.getFullName() : "" %>" class="<%= fieldErrors != null && fieldErrors.get("fullName") != null ? "input--error" : "" %>" required />
            <%
              if (fieldErrors != null && fieldErrors.get("fullName") != null) {
            %>
            <div class="form__error"><%= fieldErrors.get("fullName") %></div>
            <%
              }
            %>
          </div>
          <div class="form__group">
            <label>Email</label>
            <input type="email" name="email" placeholder="guest@email.com" value="<%= isEdit ? (editGuest.getEmail() == null ? "" : editGuest.getEmail()) : "" %>" />
          </div>
          <div class="form__group">
            <label>Phone</label>
            <input type="text" name="phone" placeholder="+94 7X XXX XXXX" value="<%= isEdit ? editGuest.getPhone() : "" %>" class="<%= fieldErrors != null && fieldErrors.get("phone") != null ? "input--error" : "" %>" required />
            <%
              if (fieldErrors != null && fieldErrors.get("phone") != null) {
            %>
            <div class="form__error"><%= fieldErrors.get("phone") %></div>
            <%
              }
            %>
          </div>
          <div class="form__group">
            <label>Address</label>
            <input type="text" name="address" placeholder="Full address" value="<%= isEdit ? editGuest.getAddress() : "" %>" class="<%= fieldErrors != null && fieldErrors.get("address") != null ? "input--error" : "" %>" required />
            <%
              if (fieldErrors != null && fieldErrors.get("address") != null) {
            %>
            <div class="form__error"><%= fieldErrors.get("address") %></div>
            <%
              }
            %>
          </div>
          <div class="form__group">
            <label>ID Type</label>
            <input type="text" name="idType" placeholder="NIC/Passport" value="<%= isEdit ? (editGuest.getIdType() == null ? "" : editGuest.getIdType()) : "" %>" />
          </div>
          <div class="form__group">
            <label>ID Number</label>
            <input type="text" name="idNumber" placeholder="ID Number" value="<%= isEdit ? (editGuest.getIdNumber() == null ? "" : editGuest.getIdNumber()) : "" %>" />
            <%
              if (fieldErrors != null && fieldErrors.get("idNumber") != null) {
            %>
            <div class="form__error"><%= fieldErrors.get("idNumber") %></div>
            <%
              }
            %>
          </div>
          <div class="form__group">
            <label>Nationality</label>
            <input type="text" name="nationality" placeholder="Sri Lankan" value="<%= isEdit ? (editGuest.getNationality() == null ? "" : editGuest.getNationality()) : "" %>" />
          </div>
          <div class="form__group">
            <label>Guest Type</label>
            <select name="guestType">
              <option value="REGULAR" <%= isEdit && "REGULAR".equals(editGuest.getGuestType()) ? "selected" : "" %>>Regular</option>
              <option value="VIP" <%= isEdit && "VIP".equals(editGuest.getGuestType()) ? "selected" : "" %>>VIP</option>
              <option value="CORPORATE" <%= isEdit && "CORPORATE".equals(editGuest.getGuestType()) ? "selected" : "" %>>Corporate</option>
            </select>
            <%
              if (fieldErrors != null && fieldErrors.get("guestType") != null) {
            %>
            <div class="form__error"><%= fieldErrors.get("guestType") %></div>
            <%
              }
            %>
          </div>
          <button class="btn btn--primary" type="submit"><%= isEdit ? "Update Guest" : "Register Guest" %></button>
          <%
            if (isEdit) {
          %>
          <a class="btn btn--outline" href="<%= request.getContextPath() %>/guests">Cancel</a>
          <%
            }
          %>
        </form>
      </div>

      <div class="panel">
        <h2>Guests</h2>
        <table class="table">
          <thead>
            <tr>
              <th>Guest ID</th>
              <th>Name</th>
              <th>Phone</th>
              <th>Nationality</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <%
              List<GuestDTO> guests = (List<GuestDTO>) request.getAttribute("guests");
              if (guests != null && !guests.isEmpty()) {
                for (GuestDTO guest : guests) {
            %>
            <tr>
              <td><%= guest.getId() %></td>
              <td><%= guest.getFullName() %></td>
              <td><%= guest.getPhone() %></td>
              <td><%= guest.getNationality() == null ? "-" : guest.getNationality() %></td>
              <td>
                <div class="table-actions">
                  <a class="btn btn--outline btn--sm" href="<%= request.getContextPath() %>/guests?editId=<%= guest.getId() %>">Edit</a>
                  <form method="post" action="<%= request.getContextPath() %>/guests" onsubmit="return confirm('Delete this guest?');">
                    <input type="hidden" name="action" value="delete" />
                    <input type="hidden" name="id" value="<%= guest.getId() %>" />
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
              <td colspan="5">No guests found.</td>
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
</body>
</html>

