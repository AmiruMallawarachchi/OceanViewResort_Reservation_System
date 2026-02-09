<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List,com.oceanview.resort.dto.DiscountDTO" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Manage Discounts</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css?v=20260206" />
</head>
<body>
  <%@ include file="/WEB-INF/partials/nav.jspf" %>
  <main class="container">
    <div class="page-header">
      <div>
        <h1 class="page-header__title">Discount Management</h1>
        <p class="muted">Create and manage guest and promotional discounts.</p>
      </div>
    </div>

    <%@ include file="/WEB-INF/partials/flash.jspf" %>
    <%@ include file="/WEB-INF/partials/field-errors.jspf" %>

    <%
      DiscountDTO editDiscount = (DiscountDTO) request.getAttribute("editDiscount");
      boolean isEdit = editDiscount != null;
    %>

    <div class="grid" style="grid-template-columns: 1fr 1fr; gap: 20px;">
      <div class="panel">
        <h2><%= isEdit ? "Edit Discount" : "Create Discount" %></h2>
        <form class="form" method="post" action="<%= request.getContextPath() %>/discounts">
          <input type="hidden" name="action" value="<%= isEdit ? "update" : "create" %>" />
          <%
            if (isEdit) {
          %>
          <input type="hidden" name="id" value="<%= editDiscount.getId() %>" />
          <%
            }
          %>
          <div class="form__group">
            <label>Name</label>
            <input type="text" name="name" placeholder="Seasonal offer" value="<%= isEdit ? editDiscount.getName() : "" %>" class="<%= fieldErrors != null && fieldErrors.get("name") != null ? "input--error" : "" %>" required />
            <%
              if (fieldErrors != null && fieldErrors.get("name") != null) {
            %>
            <div class="form__error"><%= fieldErrors.get("name") %></div>
            <%
              }
            %>
          </div>
          <div class="form__group">
            <label>Discount Type</label>
            <select name="discountType" id="discountTypeSelect" class="<%= fieldErrors != null && fieldErrors.get("discountType") != null ? "input--error" : "" %>">
              <option value="">Select type</option>
              <option value="GUEST_TYPE" <%= isEdit && "GUEST_TYPE".equals(editDiscount.getDiscountType()) ? "selected" : "" %>>Guest Type</option>
              <option value="PROMOTION" <%= isEdit && "PROMOTION".equals(editDiscount.getDiscountType()) ? "selected" : "" %>>Promotion</option>
            </select>
            <%
              if (fieldErrors != null && fieldErrors.get("discountType") != null) {
            %>
            <div class="form__error"><%= fieldErrors.get("discountType") %></div>
            <%
              }
            %>
          </div>
          <div class="form__group" id="guestTypeGroup">
            <label>Guest Type</label>
            <select name="guestType" class="<%= fieldErrors != null && fieldErrors.get("guestType") != null ? "input--error" : "" %>">
              <option value="">Select guest type</option>
              <option value="REGULAR" <%= isEdit && "REGULAR".equals(editDiscount.getGuestType()) ? "selected" : "" %>>Regular</option>
              <option value="VIP" <%= isEdit && "VIP".equals(editDiscount.getGuestType()) ? "selected" : "" %>>VIP</option>
              <option value="CORPORATE" <%= isEdit && "CORPORATE".equals(editDiscount.getGuestType()) ? "selected" : "" %>>Corporate</option>
            </select>
            <%
              if (fieldErrors != null && fieldErrors.get("guestType") != null) {
            %>
            <div class="form__error"><%= fieldErrors.get("guestType") %></div>
            <%
              }
            %>
          </div>
          <div class="form__group">
            <label>Percent (%)</label>
            <input type="number" name="percent" min="0" max="100" step="0.01" placeholder="10" value="<%= isEdit ? editDiscount.getPercent() : "" %>" class="<%= fieldErrors != null && fieldErrors.get("percent") != null ? "input--error" : "" %>" required />
            <%
              if (fieldErrors != null && fieldErrors.get("percent") != null) {
            %>
            <div class="form__error"><%= fieldErrors.get("percent") %></div>
            <%
              }
            %>
          </div>
          <div class="form__group">
            <label>Description</label>
            <input type="text" name="description" placeholder="Optional details" value="<%= isEdit ? (editDiscount.getDescription() == null ? "" : editDiscount.getDescription()) : "" %>" />
          </div>
          <div class="form__group">
            <label>Status</label>
            <select name="active">
              <option value="true" <%= !isEdit || editDiscount.isActive() ? "selected" : "" %>>Active</option>
              <option value="false" <%= isEdit && !editDiscount.isActive() ? "selected" : "" %>>Inactive</option>
            </select>
          </div>
          <button class="btn btn--primary" type="submit"><%= isEdit ? "Update Discount" : "Create Discount" %></button>
          <%
            if (isEdit) {
          %>
          <a class="btn btn--outline" href="<%= request.getContextPath() %>/discounts">Cancel</a>
          <%
            }
          %>
        </form>
      </div>

      <div class="panel">
        <h2>Discounts</h2>
        <table class="table">
          <thead>
            <tr>
              <th>Name</th>
              <th>Type</th>
              <th>Guest</th>
              <th>Percent</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <%
              List<DiscountDTO> discounts = (List<DiscountDTO>) request.getAttribute("discounts");
              if (discounts != null && !discounts.isEmpty()) {
                for (DiscountDTO discount : discounts) {
            %>
            <tr>
              <td><%= discount.getName() %></td>
              <td><%= discount.getDiscountType() %></td>
              <td><%= discount.getGuestType() == null ? "-" : discount.getGuestType() %></td>
              <td><%= discount.getPercent() %>%</td>
              <td><%= discount.isActive() ? "Active" : "Inactive" %></td>
              <td>
                <div class="table-actions">
                  <a class="btn btn--outline btn--sm" href="<%= request.getContextPath() %>/discounts?editId=<%= discount.getId() %>">Edit</a>
                  <form method="post" action="<%= request.getContextPath() %>/discounts">
                    <input type="hidden" name="action" value="toggle" />
                    <input type="hidden" name="id" value="<%= discount.getId() %>" />
                    <button class="btn btn--outline btn--sm" type="submit"><%= discount.isActive() ? "Deactivate" : "Activate" %></button>
                  </form>
                  <form method="post" action="<%= request.getContextPath() %>/discounts" onsubmit="return confirm('Delete this discount?');">
                    <input type="hidden" name="action" value="delete" />
                    <input type="hidden" name="id" value="<%= discount.getId() %>" />
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
              <td colspan="6">No discounts found.</td>
            </tr>
            <%
              }
            %>
          </tbody>
        </table>
      </div>
    </div>
  </main>
  <script>
    document.addEventListener("DOMContentLoaded", function () {
      var discountTypeSelect = document.getElementById("discountTypeSelect");
      var guestTypeGroup = document.getElementById("guestTypeGroup");
      function toggleGuestType() {
        if (!discountTypeSelect || !guestTypeGroup) {
          return;
        }
        guestTypeGroup.style.display = discountTypeSelect.value === "GUEST_TYPE" ? "block" : "none";
      }
      if (discountTypeSelect) {
        discountTypeSelect.addEventListener("change", toggleGuestType);
        toggleGuestType();
      }
    });
  </script>
  <%@ include file="/WEB-INF/partials/footer.jspf" %>
</body>
</html>
