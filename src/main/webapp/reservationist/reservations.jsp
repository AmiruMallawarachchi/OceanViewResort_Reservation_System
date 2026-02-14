<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List,com.oceanview.resort.dto.ReservationDTO,com.oceanview.resort.dto.RoomDTO,com.oceanview.resort.dto.RoomTypeDTO" %>
<% String ctx = request.getContextPath(); %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Reservations | OceanView Resort</title>
  <link rel="stylesheet" href="<%= ctx %>/assets/css/style.css?v=20260213" />
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
      String flashReservationNo = (String) session.getAttribute("flashReservationNo");
      Object flashReservationIdObj = session.getAttribute("flashReservationId");
      Long flashReservationId = flashReservationIdObj instanceof Number ? ((Number) flashReservationIdObj).longValue() : null;
    %>
    <% if (flashReservationNo != null && !flashReservationNo.isEmpty()) { %>
    <div class="panel" style="background: var(--success); color: #fff; margin-bottom: 20px;">
      <h3 style="margin-bottom: 8px;">Reservation created successfully</h3>
      <p style="margin-bottom: 12px;">Reservation number: <strong><%= flashReservationNo %></strong></p>
      <div class="table-actions" style="flex-wrap: wrap; gap: 8px;">
        <% if (flashReservationId != null) { %>
        <a href="<%= ctx %>/reservations?viewId=<%= flashReservationId %>&print=1" target="_blank" class="btn btn--outline" style="background: #fff; color: var(--success); border-color: #fff;">Print confirmation</a>
        <% } %>
        <a href="<%= ctx %>/reservations?clearSuccess=1" class="btn btn--outline" style="background: transparent; color: #fff; border-color: #fff;">Add another reservation</a>
      </div>
    </div>
    <% } %>

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
        <form class="form" method="post" action="<%= ctx %>/reservations">
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
            <input type="hidden" id="selectedRoomRate" value="" />
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
          <div id="reservationDateError" class="form__error" style="display:none;"></div>
          <% if (!isEdit) { %>
          <div class="form__group">
            <label>Estimated total</label>
            <div style="display:flex; align-items:center; gap:12px; flex-wrap:wrap;">
              <button class="btn btn--outline" type="button" id="calculateReservationTotal">Calculate</button>
              <span id="reservationTotalDisplay" class="muted">—</span>
            </div>
          </div>
          <% } %>
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
          <a class="btn btn--outline" href="<%= ctx %>/reservations">Cancel</a>
          <%
            }
          %>
        </form>
      </div>

      <div class="grid" style="gap: 20px;">
        <div class="panel">
          <h2>Room Availability</h2>
          <form class="form" method="get" action="<%= ctx %>/reservations">
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
                  <button class="btn btn--outline btn--sm" type="button" data-room-id="<%= room.getId() %>" data-room-number="<%= room.getRoomNumber() %>" data-rate="<%= room.getRoomTypeRatePerNight() != null ? room.getRoomTypeRatePerNight() : "" %>">Select</button>
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
        <h2>Search & Filter Reservations</h2>
        <form class="form" method="get" action="<%= ctx %>/reservations">
          <div class="table-filters">
            <div class="filters">
              <div class="filters__group filters__group--search">
                <label class="muted" style="display:block; font-size: 13px;">Reservation ID / Guest / Room</label>
                <input type="text" name="q" placeholder="Enter reservation no, guest name, or room" value="<%= request.getAttribute("searchQuery") != null ? request.getAttribute("searchQuery") : "" %>" />
              </div>
              <div class="filters__group">
                <label class="muted" style="display:block; font-size: 13px;">From</label>
                <input type="date" name="fromDate" id="resFromDateFilter" value="<%= request.getAttribute("filterFromDate") != null ? request.getAttribute("filterFromDate") : "" %>" />
              </div>
              <div class="filters__group">
                <label class="muted" style="display:block; font-size: 13px;">To</label>
                <input type="date" name="toDate" id="resToDateFilter" value="<%= request.getAttribute("filterToDate") != null ? request.getAttribute("filterToDate") : "" %>" />
              </div>
              <div class="filters__group">
                <label class="muted" style="display:block; font-size: 13px;">Status</label>
                <select name="status" id="resStatusFilter">
                  <option value="">All</option>
                  <option value="PENDING" <%= "PENDING".equals(request.getAttribute("filterStatus")) ? "selected" : "" %>>Pending</option>
                  <option value="CONFIRMED" <%= "CONFIRMED".equals(request.getAttribute("filterStatus")) ? "selected" : "" %>>Confirmed</option>
                  <option value="CHECKED_IN" <%= "CHECKED_IN".equals(request.getAttribute("filterStatus")) ? "selected" : "" %>>Checked-in</option>
                  <option value="CHECKED_OUT" <%= "CHECKED_OUT".equals(request.getAttribute("filterStatus")) ? "selected" : "" %>>Checked-out</option>
                  <option value="CANCELLED" <%= "CANCELLED".equals(request.getAttribute("filterStatus")) ? "selected" : "" %>>Cancelled</option>
                </select>
              </div>
              <button class="btn btn--outline btn--sm" type="submit" style="align-self: flex-end;">Search</button>
            </div>
          </div>
        </form>

        <table class="table table--striped table--compact" id="reservationsTable">
          <thead>
            <tr>
              <th>Reservation ID</th>
              <th>Guest Name</th>
              <th>Room</th>
              <th>Check-In</th>
              <th>Check-Out</th>
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
            <%
                  String resStatus = reservation.getStatus() == null ? "" : reservation.getStatus();
                  String resStatusClass = resStatus.toLowerCase().replace('_', '-');
            %>
            <tr
                data-checkin="<%= reservation.getCheckInDate() == null ? "" : reservation.getCheckInDate() %>"
                data-checkout="<%= reservation.getCheckOutDate() == null ? "" : reservation.getCheckOutDate() %>"
                data-status="<%= resStatus %>">
              <td><%= reservation.getReservationNo() %></td>
              <td><%= reservation.getGuestName() %></td>
              <td><%= reservation.getRoomNumber() == null ? "-" : reservation.getRoomNumber() %></td>
              <td><%= reservation.getCheckInDate() == null ? "-" : reservation.getCheckInDate() %></td>
              <td><%= reservation.getCheckOutDate() == null ? "-" : reservation.getCheckOutDate() %></td>
              <td>
                <span class="badge badge--status <%= resStatusClass %>"><%= resStatus %></span>
              </td>
              <td>
                <div class="table-actions">
                  <a class="btn btn--outline btn--sm" href="<%= ctx %>/reservations?viewId=<%= reservation.getId() %>">View</a>
                  <% if ("PENDING".equals(resStatus) || "CONFIRMED".equals(resStatus)) { %>
                  <form method="post" action="<%= ctx %>/reservations" style="display:inline;">
                    <input type="hidden" name="action" value="checkIn" />
                    <input type="hidden" name="id" value="<%= reservation.getId() %>" />
                    <button class="btn btn--primary btn--sm" type="submit">Check-in</button>
                  </form>
                  <% } %>
                  <% if ("CHECKED_IN".equals(resStatus)) { %>
                  <form method="post" action="<%= ctx %>/reservations" style="display:inline;">
                    <input type="hidden" name="action" value="checkOut" />
                    <input type="hidden" name="id" value="<%= reservation.getId() %>" />
                    <button class="btn btn--outline btn--sm" type="submit">Check-out</button>
                  </form>
                  <a class="btn btn--outline btn--sm" href="<%= ctx %>/bills?reservationId=<%= reservation.getId() %>">Bill</a>
                  <% } %>
                  <a class="btn btn--outline btn--sm" href="<%= ctx %>/reservations?editId=<%= reservation.getId() %>">Edit</a>
                  <% if (!"CANCELLED".equals(resStatus) && !"CHECKED_OUT".equals(resStatus)) { %>
                  <form method="post" action="<%= ctx %>/reservations" onsubmit="return confirm('Cancel this reservation?');" style="display:inline;">
                    <input type="hidden" name="action" value="cancel" />
                    <input type="hidden" name="id" value="<%= reservation.getId() %>" />
                    <button class="btn btn--outline btn--sm" type="submit">Cancel</button>
                  </form>
                  <% } %>
                </div>
              </td>
            </tr>
            <%
                }
              } else {
            %>
            <tr>
              <td colspan="7">No reservations available.</td>
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
      var selectedRoomRateInput = document.getElementById("selectedRoomRate");
      buttons.forEach(function (button) {
        button.addEventListener("click", function () {
          var roomId = button.getAttribute("data-room-id");
          var roomNumber = button.getAttribute("data-room-number");
          var rate = button.getAttribute("data-rate") || "";
          if (roomIdInput) {
            roomIdInput.value = roomId;
          }
          if (roomNumberDisplay) {
            roomNumberDisplay.value = roomNumber;
          }
          if (selectedRoomRateInput) {
            selectedRoomRateInput.value = rate;
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

      // Calculate reservation total (nights * rate)
      var calculateBtn = document.getElementById("calculateReservationTotal");
      var totalDisplay = document.getElementById("reservationTotalDisplay");
      if (calculateBtn && totalDisplay) {
        calculateBtn.addEventListener("click", function () {
          var checkInVal = reservationCheckIn && reservationCheckIn.value ? reservationCheckIn.value : "";
          var checkOutVal = reservationCheckOut && reservationCheckOut.value ? reservationCheckOut.value : "";
          var rateVal = selectedRoomRateInput && selectedRoomRateInput.value ? selectedRoomRateInput.value.replace(/[^0-9.]/g, "") : "";
          if (!checkInVal || !checkOutVal) {
            totalDisplay.textContent = "Enter check-in and check-out dates.";
            return;
          }
          var inDate = new Date(checkInVal);
          var outDate = new Date(checkOutVal);
          if (outDate <= inDate) {
            totalDisplay.textContent = "Invalid date range.";
            return;
          }
          var nights = Math.ceil((outDate - inDate) / (1000 * 60 * 60 * 24));
          var rate = parseFloat(rateVal);
          if (isNaN(rate) || rate < 0) {
            totalDisplay.textContent = "Select a room from availability to see rate.";
            return;
          }
          var total = (nights * rate).toFixed(2);
          totalDisplay.textContent = "Total: " + total + " (".concat(nights, " night(s) \u00d7 ", rateVal, ")");
        });
      }

      // Client-side date validation for reservation form
      var reservationForm = document.querySelector('form[action$="/reservations"][method="post"]');
      var dateError = document.getElementById("reservationDateError");
      if (reservationForm && reservationCheckIn && reservationCheckOut && dateError) {
        reservationForm.addEventListener("submit", function (e) {
          var inVal = reservationCheckIn.value;
          var outVal = reservationCheckOut.value;
          dateError.style.display = "none";
          dateError.textContent = "";
          reservationCheckIn.classList.remove("input--error");
          reservationCheckOut.classList.remove("input--error");
          if (!inVal || !outVal) {
            return;
          }
          var inDate = new Date(inVal);
          var outDate = new Date(outVal);
          var today = new Date();
          today.setHours(0, 0, 0, 0);
          if (outDate <= inDate) {
            e.preventDefault();
            dateError.textContent = "Check-out date must be after check-in date.";
            dateError.style.display = "block";
            reservationCheckIn.classList.add("input--error");
            reservationCheckOut.classList.add("input--error");
            return;
          }
          if (!<%= isEdit %> && inDate < today) {
            e.preventDefault();
            dateError.textContent = "Check-in date cannot be in the past for a new reservation.";
            dateError.style.display = "block";
            reservationCheckIn.classList.add("input--error");
          }
        });
      }

      // Client-side filters for reservation list
      var fromFilter = document.getElementById("resFromDateFilter");
      var toFilter = document.getElementById("resToDateFilter");
      var statusFilter = document.getElementById("resStatusFilter");
      var table = document.getElementById("reservationsTable");

      function applyFilters() {
        if (!table) return;
        var fromVal = fromFilter && fromFilter.value ? fromFilter.value : null;
        var toVal = toFilter && toFilter.value ? toFilter.value : null;
        var statusVal = statusFilter && statusFilter.value ? statusFilter.value : null;
        var rows = table.querySelectorAll("tbody tr");
        rows.forEach(function (row) {
          var checkIn = row.getAttribute("data-checkin") || "";
          var checkOut = row.getAttribute("data-checkout") || "";
          var status = row.getAttribute("data-status") || "";
          var visible = true;
          if (fromVal && checkIn && checkIn < fromVal) {
            visible = false;
          }
          if (toVal && checkOut && checkOut > toVal) {
            visible = false;
          }
          if (statusVal && status.toUpperCase() !== statusVal.toUpperCase()) {
            visible = false;
          }
          row.style.display = visible ? "" : "none";
        });
      }

      if (fromFilter) fromFilter.addEventListener("change", applyFilters);
      if (toFilter) toFilter.addEventListener("change", applyFilters);
      if (statusFilter) statusFilter.addEventListener("change", applyFilters);
    });
  </script>
  <%@ include file="/WEB-INF/partials/footer.jspf" %>
</body>
</html>

