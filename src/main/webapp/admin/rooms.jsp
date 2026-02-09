<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List,com.oceanview.resort.dto.RoomTypeDTO,com.oceanview.resort.dto.RoomDTO" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Manage Rooms</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css?v=20260206" />
</head>
<body>
  <%@ include file="/WEB-INF/partials/nav.jspf" %>
  <main class="container">
    <div class="page-header">
      <div>
        <h1 class="page-header__title">Room & Room Type Management</h1>
        <p class="muted">Maintain room inventory and room type pricing.</p>
      </div>
    </div>

    <%@ include file="/WEB-INF/partials/flash.jspf" %>
    <%@ include file="/WEB-INF/partials/field-errors.jspf" %>

    <%
      RoomTypeDTO editRoomType = (RoomTypeDTO) request.getAttribute("editRoomType");
      RoomDTO editRoom = (RoomDTO) request.getAttribute("editRoom");
      boolean editTypeMode = editRoomType != null;
      boolean editRoomMode = editRoom != null;
    %>

    <div class="grid" style="grid-template-columns: 1fr 1fr; gap: 20px;">
      <div class="panel">
        <h2><%= editTypeMode ? "Edit Room Type" : "Add Room Type" %></h2>
        <form class="form" method="post" action="<%= request.getContextPath() %>/room-types">
          <input type="hidden" name="action" value="<%= editTypeMode ? "update" : "create" %>" />
          <%
            if (editTypeMode) {
          %>
          <input type="hidden" name="id" value="<%= editRoomType.getId() %>" />
          <%
            }
          %>
          <div class="form__group">
            <label>Type Name</label>
            <input type="text" name="typeName" placeholder="Deluxe" value="<%= editTypeMode ? editRoomType.getTypeName() : "" %>" class="<%= fieldErrors != null && fieldErrors.get("typeName") != null ? "input--error" : "" %>" required />
            <%
              if (fieldErrors != null && fieldErrors.get("typeName") != null) {
            %>
            <div class="form__error"><%= fieldErrors.get("typeName") %></div>
            <%
              }
            %>
          </div>
          <div class="form__group">
            <label>Rate Per Night</label>
            <input type="number" name="ratePerNight" placeholder="15000" value="<%= editTypeMode ? editRoomType.getRatePerNight() : "" %>" class="<%= fieldErrors != null && fieldErrors.get("ratePerNight") != null ? "input--error" : "" %>" required />
            <%
              if (fieldErrors != null && fieldErrors.get("ratePerNight") != null) {
            %>
            <div class="form__error"><%= fieldErrors.get("ratePerNight") %></div>
            <%
              }
            %>
          </div>
          <div class="form__group">
            <label>Max Occupancy</label>
            <input type="number" name="maxOccupancy" placeholder="2" value="<%= editTypeMode ? editRoomType.getMaxOccupancy() : "" %>" class="<%= fieldErrors != null && fieldErrors.get("maxOccupancy") != null ? "input--error" : "" %>" required />
            <%
              if (fieldErrors != null && fieldErrors.get("maxOccupancy") != null) {
            %>
            <div class="form__error"><%= fieldErrors.get("maxOccupancy") %></div>
            <%
              }
            %>
          </div>
          <div class="form__group">
            <label>Amenities</label>
            <input type="text" name="amenities" placeholder="WiFi, AC, Balcony" value="<%= editTypeMode ? editRoomType.getAmenities() : "" %>" />
          </div>
          <button class="btn btn--primary" type="submit"><%= editTypeMode ? "Update Type" : "Save Type" %></button>
          <%
            if (editTypeMode) {
          %>
          <a class="btn btn--outline" href="<%= request.getContextPath() %>/rooms">Cancel</a>
          <%
            }
          %>
        </form>
      </div>

      <div class="panel">
        <h2><%= editRoomMode ? "Edit Room" : "Add Room" %></h2>
        <form class="form" method="post" action="<%= request.getContextPath() %>/rooms">
          <input type="hidden" name="action" value="<%= editRoomMode ? "update" : "create" %>" />
          <%
            if (editRoomMode) {
          %>
          <input type="hidden" name="id" value="<%= editRoom.getId() %>" />
          <%
            }
          %>
          <div class="form__group">
            <label>Room Number</label>
            <input type="text" name="roomNumber" placeholder="101" value="<%= editRoomMode ? editRoom.getRoomNumber() : "" %>" class="<%= fieldErrors != null && fieldErrors.get("roomNumber") != null ? "input--error" : "" %>" required />
            <%
              if (fieldErrors != null && fieldErrors.get("roomNumber") != null) {
            %>
            <div class="form__error"><%= fieldErrors.get("roomNumber") %></div>
            <%
              }
            %>
          </div>
          <div class="form__group">
            <label>Room Type</label>
            <select name="roomTypeId">
              <%
                List<RoomTypeDTO> roomTypes = (List<RoomTypeDTO>) request.getAttribute("roomTypes");
                if (roomTypes != null && !roomTypes.isEmpty()) {
                  for (RoomTypeDTO type : roomTypes) {
                    String selected = editRoomMode && editRoom.getRoomTypeId() == type.getId() ? "selected" : "";
              %>
              <option value="<%= type.getId() %>" <%= selected %>><%= type.getTypeName() %></option>
              <%
                  }
                } else {
              %>
              <option value="0">No room types available</option>
              <%
                }
              %>
            </select>
            <%
              if (fieldErrors != null && fieldErrors.get("roomTypeId") != null) {
            %>
            <div class="form__error"><%= fieldErrors.get("roomTypeId") %></div>
            <%
              }
            %>
          </div>
          <div class="form__group">
            <label>Floor</label>
            <input type="number" name="floor" placeholder="1" value="<%= editRoomMode ? editRoom.getFloor() : "" %>" class="<%= fieldErrors != null && fieldErrors.get("floor") != null ? "input--error" : "" %>" required />
            <%
              if (fieldErrors != null && fieldErrors.get("floor") != null) {
            %>
            <div class="form__error"><%= fieldErrors.get("floor") %></div>
            <%
              }
            %>
          </div>
          <div class="form__group">
            <label>Full Access</label>
            <label style="display:flex; align-items:center; gap:8px;">
              <input type="checkbox" name="fullAccess" value="1" <%= editRoomMode && editRoom.isFullAccess() ? "checked" : "" %> />
              <span>Grant full access amenities</span>
            </label>
          </div>
          <%
            if (editRoomMode) {
          %>
          <div class="form__group">
            <label>Status</label>
            <select name="status">
              <option value="AVAILABLE" <%= "AVAILABLE".equals(editRoom.getStatus()) ? "selected" : "" %>>AVAILABLE</option>
              <option value="OCCUPIED" <%= "OCCUPIED".equals(editRoom.getStatus()) ? "selected" : "" %>>OCCUPIED</option>
              <option value="MAINTENANCE" <%= "MAINTENANCE".equals(editRoom.getStatus()) ? "selected" : "" %>>MAINTENANCE</option>
              <option value="RESERVED" <%= "RESERVED".equals(editRoom.getStatus()) ? "selected" : "" %>>RESERVED</option>
            </select>
          </div>
          <%
            }
          %>
          <button class="btn btn--primary" type="submit"><%= editRoomMode ? "Update Room" : "Add Room" %></button>
          <%
            if (editRoomMode) {
          %>
          <a class="btn btn--outline" href="<%= request.getContextPath() %>/rooms">Cancel</a>
          <%
            }
          %>
        </form>
      </div>
    </div>

    <div class="section panel">
      <h2>Room Types</h2>
      <table class="table">
        <thead>
          <tr>
            <th>Type</th>
            <th>Rate</th>
            <th>Max</th>
            <th>Amenities</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <%
            List<RoomTypeDTO> roomTypesList = (List<RoomTypeDTO>) request.getAttribute("roomTypes");
            if (roomTypesList != null && !roomTypesList.isEmpty()) {
              for (RoomTypeDTO type : roomTypesList) {
          %>
          <tr>
            <td><%= type.getTypeName() %></td>
            <td><%= type.getRatePerNight() %></td>
            <td><%= type.getMaxOccupancy() %></td>
            <td><%= type.getAmenities() %></td>
            <td>
              <div class="table-actions">
                <a class="btn btn--outline btn--sm" href="<%= request.getContextPath() %>/rooms?editTypeId=<%= type.getId() %>">Edit</a>
                <form method="post" action="<%= request.getContextPath() %>/room-types" onsubmit="return confirm('Delete this room type?');">
                  <input type="hidden" name="action" value="delete" />
                  <input type="hidden" name="id" value="<%= type.getId() %>" />
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
            <td colspan="5">No room types available.</td>
          </tr>
          <%
            }
          %>
        </tbody>
      </table>
    </div>

    <div class="section panel">
      <h2>Room Inventory</h2>
      <table class="table">
        <thead>
          <tr>
            <th>Room</th>
            <th>Type</th>
            <th>Floor</th>
            <th>Amenities</th>
            <th>Access</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <%
            List<RoomDTO> rooms = (List<RoomDTO>) request.getAttribute("rooms");
            if (rooms != null && !rooms.isEmpty()) {
              for (RoomDTO room : rooms) {
          %>
          <tr>
            <td><%= room.getRoomNumber() %></td>
            <td><%= room.getRoomTypeName() %></td>
            <td><%= room.getFloor() %></td>
            <td><%= room.getRoomTypeAmenities() %></td>
            <td><%= room.isFullAccess() ? "FULL ACCESS" : "COMMON" %></td>
            <td><%= room.getStatus() %></td>
            <td>
              <div class="table-actions">
                <a class="btn btn--outline btn--sm" href="<%= request.getContextPath() %>/rooms?editId=<%= room.getId() %>">Edit</a>
                <form method="post" action="<%= request.getContextPath() %>/rooms" onsubmit="return confirm('Delete this room?');">
                  <input type="hidden" name="action" value="delete" />
                  <input type="hidden" name="id" value="<%= room.getId() %>" />
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
            <td colspan="7">No rooms available.</td>
          </tr>
          <%
            }
          %>
        </tbody>
      </table>
    </div>
  </main>
  <%@ include file="/WEB-INF/partials/footer.jspf" %>
</body>
</html>

