<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List,com.oceanview.resort.dto.RoomTypeDTO,com.oceanview.resort.dto.RoomDTO" %>
<% String ctx = request.getContextPath(); %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Rooms | OceanView Resort</title>
  <link rel="stylesheet" href="<%= ctx %>/assets/css/style.css?v=20260213" />
</head>
<body>
  <%@ include file="/WEB-INF/partials/nav.jspf" %>
  <main class="container">
    <%@ include file="/WEB-INF/partials/flash.jspf" %>
    <%@ include file="/WEB-INF/partials/field-errors.jspf" %>

    <%
      RoomTypeDTO editRoomType = (RoomTypeDTO) request.getAttribute("editRoomType");
      RoomDTO editRoom = (RoomDTO) request.getAttribute("editRoom");
      boolean editTypeMode = editRoomType != null;
      boolean editRoomMode = editRoom != null;
      List<RoomDTO> rooms = (List<RoomDTO>) request.getAttribute("rooms");
      List<RoomTypeDTO> roomTypes = (List<RoomTypeDTO>) request.getAttribute("roomTypes");
      int totalRooms = rooms == null ? 0 : rooms.size();
      long availableCount = rooms == null ? 0 : rooms.stream().filter(r -> "AVAILABLE".equalsIgnoreCase(r.getStatus())).count();
      long occupiedCount = rooms == null ? 0 : rooms.stream().filter(r -> "OCCUPIED".equalsIgnoreCase(r.getStatus()) || "RESERVED".equalsIgnoreCase(r.getStatus())).count();
      long maintenanceCount = rooms == null ? 0 : rooms.stream().filter(r -> "MAINTENANCE".equalsIgnoreCase(r.getStatus())).count();
      String roomFilter = request.getParameter("type");
      String roomSearch = request.getParameter("q");
    %>

    <div class="page-header page-header--row">
      <div>
        <h1 class="page-header__title">Rooms</h1>
        <p class="muted"><%= totalRooms %> total rooms</p>
      </div>
      <a class="btn btn--primary" href="<%= ctx %>/rooms#room-form">Add Room</a>
    </div>

    <div class="room-summary-cards">
      <div class="room-summary-card room-summary-card--available">
        <div class="room-summary-card__value"><%= availableCount %></div>
        <div class="room-summary-card__label">Available</div>
      </div>
      <div class="room-summary-card room-summary-card--occupied">
        <div class="room-summary-card__value"><%= occupiedCount %></div>
        <div class="room-summary-card__label">Occupied</div>
      </div>
      <div class="room-summary-card room-summary-card--maintenance">
        <div class="room-summary-card__value"><%= maintenanceCount %></div>
        <div class="room-summary-card__label">Maintenance</div>
      </div>
    </div>

    <form method="get" action="<%= ctx %>/rooms" class="filters" style="margin-bottom: 16px;">
      <div class="filters__group filters__group--search">
        <input type="text" name="q" placeholder="Search room number or type..." value="<%= roomSearch == null ? "" : roomSearch %>" />
      </div>
      <div class="room-type-tabs" style="flex: 1; display: flex; flex-wrap: wrap; gap: 8px; align-items: center;">
        <a href="<%= ctx %>/rooms<%= roomSearch != null && !roomSearch.isEmpty() ? "?q=" + java.net.URLEncoder.encode(roomSearch, "UTF-8") : "" %>" style="padding: 8px 16px; border-radius: 10px; border: 1px solid var(--border); background: <%= (roomFilter == null || roomFilter.isEmpty()) ? "var(--brand)" : "var(--card)" %>; color: <%= (roomFilter == null || roomFilter.isEmpty()) ? "#fff" : "var(--text)" %>; text-decoration: none; font-size: 14px; font-weight: 500;">All</a>
        <% if (roomTypes != null) {
          for (RoomTypeDTO t : roomTypes) {
            String typeName = t.getTypeName();
            String typeId = String.valueOf(t.getId());
            boolean active = typeId.equals(roomFilter);
        %>
        <a href="<%= ctx %>/rooms?type=<%= typeId %><%= roomSearch != null && !roomSearch.isEmpty() ? "&q=" + java.net.URLEncoder.encode(roomSearch, "UTF-8") : "" %>" style="padding: 8px 16px; border-radius: 10px; border: 1px solid var(--border); background: <%= active ? "var(--brand)" : "var(--card)" %>; color: <%= active ? "#fff" : "var(--text)" %>; text-decoration: none; font-size: 14px; font-weight: 500;"><%= typeName %></a>
        <% }
        } %>
      </div>
    </form>

    <div class="rooms-grid" style="margin-bottom: 32px;">
      <%
        if (rooms != null) {
          for (RoomDTO room : rooms) {
            if (roomFilter != null && !roomFilter.isEmpty()) {
              try {
                if (room.getRoomTypeId() != Long.parseLong(roomFilter)) continue;
              } catch (NumberFormatException e) { }
            }
            if (roomSearch != null && !roomSearch.isEmpty()) {
              String rn = room.getRoomNumber() != null ? room.getRoomNumber() : "";
              String tn = room.getRoomTypeName() != null ? room.getRoomTypeName() : "";
              if (!rn.toLowerCase().contains(roomSearch.toLowerCase()) && !tn.toLowerCase().contains(roomSearch.toLowerCase())) continue;
            }
            String status = room.getStatus() != null ? room.getStatus() : "AVAILABLE";
            String pillClass = "pill--available";
            if ("OCCUPIED".equalsIgnoreCase(status) || "RESERVED".equalsIgnoreCase(status)) pillClass = "pill--occupied";
            else if ("MAINTENANCE".equalsIgnoreCase(status)) pillClass = "pill--maintenance";
            String rateDisplay = room.getRoomTypeRatePerNight() != null ? "$" + room.getRoomTypeRatePerNight() + "/night" : "—";
            int cap = room.getRoomTypeMaxOccupancy();
            String capacityDisplay = cap > 0 ? cap + " guests" : "—";
      %>
      <div class="room-card">
        <div class="room-card__header">
          <div class="room-card__number">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l18 0"/><path d="M3 15l18 0"/><path d="M5 9V6a2 2 0 012-2h10a2 2 0 012 2v3"/><path d="M5 15v3a2 2 0 002 2h10a2 2 0 002-2v-3"/></svg>
            #<%= room.getRoomNumber() != null ? room.getRoomNumber() : "" %>
          </div>
          <span class="pill <%= pillClass %>"><%= status %></span>
        </div>
        <div class="room-card__detail">Type: <%= room.getRoomTypeName() != null ? room.getRoomTypeName() : "—" %></div>
        <div class="room-card__detail">Floor: <%= room.getFloor() %></div>
        <div class="room-card__detail">Capacity: <%= capacityDisplay %></div>
        <div class="room-card__rate"><%= rateDisplay %></div>
        <div class="table-actions" style="margin-top: 12px;">
          <a class="btn btn--outline btn--sm" href="<%= ctx %>/rooms?editId=<%= room.getId() %>">Edit</a>
          <form method="post" action="<%= ctx %>/rooms" onsubmit="return confirm('Delete this room?');" style="display:inline;">
            <input type="hidden" name="action" value="delete" />
            <input type="hidden" name="id" value="<%= room.getId() %>" />
            <button class="btn btn--outline btn--sm" type="submit">Delete</button>
          </form>
        </div>
      </div>
      <% }
      } %>
    </div>

    <div class="grid" style="grid-template-columns: 1fr 1fr; gap: 20px;" id="room-form">
      <div class="panel">
        <h2><%= editTypeMode ? "Edit Room Type" : "Add Room Type" %></h2>
        <form class="form" method="post" action="<%= ctx %>/room-types">
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
          <a class="btn btn--outline" href="<%= ctx %>/rooms">Cancel</a>
          <%
            }
          %>
        </form>
      </div>

      <div class="panel">
        <h2><%= editRoomMode ? "Edit Room" : "Add Room" %></h2>
        <form class="form" method="post" action="<%= ctx %>/rooms">
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
          <a class="btn btn--outline" href="<%= ctx %>/rooms">Cancel</a>
          <%
            }
          %>
        </form>
      </div>
    </div>

    <div class="section panel">
      <h2>Room Types</h2>
      <table class="table table--striped">
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
            if (roomTypes != null && !roomTypes.isEmpty()) {
              for (RoomTypeDTO type : roomTypes) {
          %>
          <tr>
            <td><%= type.getTypeName() %></td>
            <td><%= type.getRatePerNight() %></td>
            <td><%= type.getMaxOccupancy() %></td>
            <td><%= type.getAmenities() %></td>
            <td>
              <div class="table-actions">
                <a class="btn btn--outline btn--sm" href="<%= ctx %>/rooms?editTypeId=<%= type.getId() %>">Edit</a>
                <form method="post" action="<%= ctx %>/room-types" onsubmit="return confirm('Delete this room type?');" style="display:inline;">
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
          <% } %>
        </tbody>
      </table>
    </div>
  </main>
  <%@ include file="/WEB-INF/partials/footer.jspf" %>
</body>
</html>

