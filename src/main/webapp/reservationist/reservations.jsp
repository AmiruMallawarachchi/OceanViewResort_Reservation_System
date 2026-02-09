<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List,com.oceanview.resort.dto.ReservationDTO,com.oceanview.resort.dto.RoomDTO,com.oceanview.resort.dto.RoomTypeDTO" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Reservation Management</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css?v=20260206" />
</head>
<body>
  <%@ include file="/WEB-INF/partials/nav.jspf" %>
  <main class="container">
    <div class="page-header">
      <div>
        <h1 class="page-header__title">Reservations</h1>
        <p class="muted">Create, update, and cancel reservations.</p>
      </div>
    </div>

    <%@ include file="/WEB-INF/partials/flash.jspf" %>
    <%@ include file="/WEB-INF/partials/field-errors.jspf" %>

    <%
      ReservationDTO editReservation = (ReservationDTO) request.getAttribute("editReservation");
      boolean isEdit = editReservation != null;
    List<RoomTypeDTO> roomTypes = (List<RoomTypeDTO>) request.getAttribute("roomTypes");
    List<RoomDTO> availableRooms = (List<RoomDTO>) request.getAttribute("availableRooms");
    String availabilityCheckIn = (String) request.getAttribute("availabilityCheckIn");
    String availabilityCheckOut = (String) request.getAttribute("availabilityCheckOut");
    String availabilityRoomTypeId = (String) request.getAttribute("availabilityRoomTypeId");
    String availabilityRoomQuery = (String) request.getAttribute("availabilityRoomQuery");
    String availabilityError = (String) request.getAttribute("availabilityError");
    %>

    <div class="grid" style="grid-template-columns: minmax(320px, 420px) 1fr; gap: 20px;">
      <div class="panel">
        <h2><%= isEdit ? "Edit Reservation" : "Add New Reservation" %></h2>
        <form class="form" method="post" action="<%= request.getContextPath() %>/reservations">
          <input type="hidden" name="action" value="<%= isEdit ? "update" : "create" %>" />
          <%
            if (availabilityCheckIn != null && !availabilityCheckIn.isEmpty()) { %>
          <input type="hidden" name="availCheckIn" value="<%= availabilityCheckIn %>" />
          <% }
            if (availabilityCheckOut != null && !availabilityCheckOut.isEmpty()) { %>
          <input type="hidden" name="availCheckOut" value="<%= availabilityCheckOut %>" />
          <% }
            if (availabilityRoomTypeId != null && !availabilityRoomTypeId.isEmpty()) { %>
          <input type="hidden" name="roomTypeId" value="<%= availabilityRoomTypeId %>" />
          <% }
            if (availabilityRoomQuery != null && !availabilityRoomQuery.isEmpty()) { %>
          <input type="hidden" name="roomQ" value="<%= availabilityRoomQuery %>" />
          <% }
            if (isEdit) {
          %>
          <input type="hidden" name="id" value="<%= editReservation.getId() %>" />
          <%
            }
          %>
          <div class="form__group">
            <label>Guest ID</label>
            <input type="number" name="guestId" placeholder="Guest ID" value="<%= isEdit ? editReservation.getGuestId() : "" %>" class="<%= fieldErrors != null && fieldErrors.get("guestId") != null ? "input--error" : "" %>" required />
            <%
              if (fieldErrors != null && fieldErrors.get("guestId") != null) {
            %>
            <div class="form__error"><%= fieldErrors.get("guestId") %></div>
            <%
              }
            %>
          </div>
          <div class="form__group">
            <label>Room ID</label>
            <input type="number" id="roomIdInput" name="roomId" placeholder="Select from availability" value="<%= isEdit ? editReservation.getRoomId() : "" %>" class="<%= fieldErrors != null && fieldErrors.get("roomId") != null ? "input--error" : "" %>" required />
            <%
              if (fieldErrors != null && fieldErrors.get("roomId") != null) {
            %>
            <div class="form__error"><%= fieldErrors.get("roomId") %></div>
            <%
              }
            %>
          </div>
          <div class="form__group">
            <label>Room Number</label>
            <input type="text" id="roomNumberDisplay" placeholder="Select from availability" value="<%= isEdit ? editReservation.getRoomNumber() : "" %>" readonly />
          </div>
          <div class="form__group">
            <label>Check-In</label>
            <input type="date" name="checkInDate" value="<%= isEdit ? editReservation.getCheckInDate() : "" %>" class="<%= fieldErrors != null && fieldErrors.get("checkInDate") != null ? "input--error" : "" %>" required />
            <%
              if (fieldErrors != null && fieldErrors.get("checkInDate") != null) {
            %>
            <div class="form__error"><%= fieldErrors.get("checkInDate") %></div>
            <%
              }
            %>
          </div>
          <div class="form__group">
            <label>Check-Out</label>
            <input type="date" name="checkOutDate" value="<%= isEdit ? editReservation.getCheckOutDate() : "" %>" class="<%= fieldErrors != null && fieldErrors.get("checkOutDate") != null ? "input--error" : "" %>" required />
            <%
              if (fieldErrors != null && fieldErrors.get("checkOutDate") != null) {
            %>
            <div class="form__error"><%= fieldErrors.get("checkOutDate") %></div>
            <%
              }
            %>
          </div>
          <%
            if (isEdit) {
          %>
          <div class="form__group">
            <label>Status</label>
            <select name="status">
              <option value="PENDING" <%= "PENDING".equals(editReservation.getStatus()) ? "selected" : "" %>>PENDING</option>
              <option value="CONFIRMED" <%= "CONFIRMED".equals(editReservation.getStatus()) ? "selected" : "" %>>CONFIRMED</option>
              <option value="CHECKED_IN" <%= "CHECKED_IN".equals(editReservation.getStatus()) ? "selected" : "" %>>CHECKED_IN</option>
              <option value="CHECKED_OUT" <%= "CHECKED_OUT".equals(editReservation.getStatus()) ? "selected" : "" %>>CHECKED_OUT</option>
              <option value="CANCELLED" <%= "CANCELLED".equals(editReservation.getStatus()) ? "selected" : "" %>>CANCELLED</option>
            </select>
          </div>
          <%
            }
          %>
          <button class="btn btn--primary" type="submit"><%= isEdit ? "Update Reservation" : "Save Reservation" %></button>
          <%
            if (isEdit) {
          %>
          <a class="btn btn--outline" href="<%= request.getContextPath() %>/reservations">Cancel</a>
          <%
            }
          %>
        </form>
      </div>

      <div class="grid" style="gap: 20px;">
        <div class="panel">
          <h2>Room Availability</h2>
          <form class="form" method="get" action="<%= request.getContextPath() %>/reservations">
            <div class="form__group">
              <label>Check-In</label>
              <input type="date" name="availCheckIn" value="<%= availabilityCheckIn == null ? "" : availabilityCheckIn %>" required />
            </div>
            <div class="form__group">
              <label>Check-Out</label>
              <input type="date" name="availCheckOut" value="<%= availabilityCheckOut == null ? "" : availabilityCheckOut %>" required />
            </div>
            <div class="form__group">
              <label>Room Type</label>
              <select name="roomTypeId">
                <option value="">All Types</option>
                <%
                  if (roomTypes != null) {
                    for (RoomTypeDTO type : roomTypes) {
                %>
                <option value="<%= type.getId() %>" <%= String.valueOf(type.getId()).equals(availabilityRoomTypeId) ? "selected" : "" %>><%= type.getTypeName() %></option>
                <%
                    }
                  }
                %>
              </select>
            </div>
            <div class="form__group">
              <label>Room Search</label>
              <input type="text" name="roomQ" placeholder="Room number" value="<%= availabilityRoomQuery == null ? "" : availabilityRoomQuery %>" />
            </div>
            <button class="btn btn--outline" type="submit">Find Available Rooms</button>
          </form>

          <%
            if (availabilityError != null) {
          %>
          <div class="form__error" style="margin-top: 10px;"><%= availabilityError %></div>
          <%
            } else if (availabilityCheckIn != null && availabilityCheckOut != null) {
          %>
          <table class="table">
            <thead>
              <tr>
                <th>Room</th>
                <th>Type</th>
                <th>Rate</th>
                <th>Max Occupancy</th>
                <th>Floor</th>
                <th>Status</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              <%
                if (availableRooms != null && !availableRooms.isEmpty()) {
                  for (RoomDTO room : availableRooms) {
              %>
              <tr>
                <td>#<%= room.getRoomNumber() %> (ID <%= room.getId() %>)</td>
                <td><%= room.getRoomTypeName() == null ? "-" : room.getRoomTypeName() %></td>
                <td><%= room.getRoomTypeRatePerNight() == null ? "-" : room.getRoomTypeRatePerNight() %></td>
                <td><%= room.getRoomTypeMaxOccupancy() == 0 ? "-" : room.getRoomTypeMaxOccupancy() %></td>
                <td><%= room.getFloor() %></td>
                <td><%= room.getStatus() == null ? "-" : room.getStatus() %></td>
                <td>
                  <button class="btn btn--outline btn--sm" type="button" data-room-id="<%= room.getId() %>" data-room-number="<%= room.getRoomNumber() %>">Select</button>
                </td>
              </tr>
              <%
                  }
                } else {
              %>
              <tr>
                <td colspan="7">No rooms available for the selected dates.</td>
              </tr>
              <%
                }
              %>
            </tbody>
          </table>
          <%
            }
          %>
      </div>

      <div class="panel">
        <h2>Search Reservation</h2>
        <form class="form" method="get" action="<%= request.getContextPath() %>/reservations">
          <div class="form__group">
            <label>Reservation ID / Guest</label>
            <input type="text" name="q" placeholder="Enter ID, guest, or room" value="<%= request.getAttribute("searchQuery") != null ? request.getAttribute("searchQuery") : "" %>" />
          </div>
          <button class="btn btn--outline" type="submit">Search</button>
        </form>

        <table class="table">
          <thead>
            <tr>
              <th>Reservation</th>
              <th>Guest</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <%
              List<ReservationDTO> reservations = (List<ReservationDTO>) request.getAttribute("reservations");
              if (reservations != null && !reservations.isEmpty()) {
                for (ReservationDTO reservation : reservations) {
            %>
            <tr>
              <td><%= reservation.getReservationNo() %></td>
              <td><%= reservation.getGuestName() %></td>
              <td><%= reservation.getStatus() %></td>
              <td>
                <div class="table-actions">
                  <a class="btn btn--outline btn--sm" href="<%= request.getContextPath() %>/reservations?editId=<%= reservation.getId() %>">Edit</a>
                  <form method="post" action="<%= request.getContextPath() %>/reservations" onsubmit="return confirm('Cancel this reservation?');">
                    <input type="hidden" name="action" value="cancel" />
                    <input type="hidden" name="id" value="<%= reservation.getId() %>" />
                    <button class="btn btn--outline btn--sm" type="submit">Cancel</button>
                  </form>
                </div>
              </td>
            </tr>
            <%
                }
              } else {
            %>
            <tr>
              <td colspan="4">No reservations available.</td>
            </tr>
            <%
              }
            %>
          </tbody>
        </table>
        </div>
      </div>
    </div>
  </main>
  <script>
    document.addEventListener("DOMContentLoaded", function () {
      var roomIdInput = document.getElementById("roomIdInput");
      var roomNumberDisplay = document.getElementById("roomNumberDisplay");
      var reservationCheckIn = document.querySelector('input[name="checkInDate"]');
      var reservationCheckOut = document.querySelector('input[name="checkOutDate"]');
      var availabilityCheckIn = document.querySelector('input[name="availCheckIn"]');
      var availabilityCheckOut = document.querySelector('input[name="availCheckOut"]');
      var buttons = document.querySelectorAll("[data-room-id]");
      buttons.forEach(function (button) {
        button.addEventListener("click", function () {
          var roomId = button.getAttribute("data-room-id");
          var roomNumber = button.getAttribute("data-room-number");
          if (roomIdInput) {
            roomIdInput.value = roomId;
          }
          if (roomNumberDisplay) {
            roomNumberDisplay.value = roomNumber;
          }
          if (reservationCheckIn && availabilityCheckIn) {
            reservationCheckIn.value = availabilityCheckIn.value || reservationCheckIn.value;
          }
          if (reservationCheckOut && availabilityCheckOut) {
            reservationCheckOut.value = availabilityCheckOut.value || reservationCheckOut.value;
          }
          if (roomIdInput) {
            roomIdInput.focus();
          }
        });
      });
    });
  </script>
  <%@ include file="/WEB-INF/partials/footer.jspf" %>
</body>
</html>

