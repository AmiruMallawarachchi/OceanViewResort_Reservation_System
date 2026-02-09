<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.ArrayList,java.util.HashMap,java.util.List,java.util.Map,com.oceanview.resort.dto.BillDTO,com.oceanview.resort.dto.DiscountDTO,com.oceanview.resort.dto.ReservationDTO" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Billing | Reservationist</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css?v=20260206" />
</head>
<body>
  <%@ include file="/WEB-INF/partials/nav.jspf" %>
  <main class="container">
    <div class="page-header">
      <div>
        <h1 class="page-header__title">Billing</h1>
        <p class="muted">Calculate and print guest bills.</p>
      </div>
    </div>

    <%@ include file="/WEB-INF/partials/flash.jspf" %>
    <%@ include file="/WEB-INF/partials/field-errors.jspf" %>

    <%
      List<DiscountDTO> activeDiscounts = (List<DiscountDTO>) request.getAttribute("activeDiscounts");
      List<DiscountDTO> promotionDiscounts = new ArrayList<>();
      Map<String, DiscountDTO> guestTypeDiscounts = new HashMap<>();
      if (activeDiscounts != null) {
        for (DiscountDTO discount : activeDiscounts) {
          if ("GUEST_TYPE".equals(discount.getDiscountType()) && discount.getGuestType() != null) {
            DiscountDTO existing = guestTypeDiscounts.get(discount.getGuestType());
            double currentPercent = discount.getPercent() == null || discount.getPercent().isBlank() ? 0 : Double.parseDouble(discount.getPercent());
            double existingPercent = existing == null || existing.getPercent() == null || existing.getPercent().isBlank() ? -1 : Double.parseDouble(existing.getPercent());
            if (existing == null || currentPercent > existingPercent) {
              guestTypeDiscounts.put(discount.getGuestType(), discount);
            }
          } else if ("PROMOTION".equals(discount.getDiscountType())) {
            promotionDiscounts.add(discount);
          }
        }
      }
    %>

    <div class="grid" style="grid-template-columns: minmax(320px, 420px) 1fr; gap: 20px;">
      <div class="panel">
        <form class="form" method="post" action="<%= request.getContextPath() %>/bills">
          <div class="form__group">
            <label>Reservation ID</label>
            <input type="number" id="reservationIdInput" name="reservationId" placeholder="Reservation ID" class="<%= fieldErrors != null && fieldErrors.get("reservationId") != null ? "input--error" : "" %>" required />
            <%
              if (fieldErrors != null && fieldErrors.get("reservationId") != null) {
            %>
            <div class="form__error"><%= fieldErrors.get("reservationId") %></div>
            <%
              }
            %>
          </div>
          <div class="form__group">
            <label>Guest</label>
            <input type="text" id="guestNameDisplay" placeholder="Select a reservation" readonly />
          </div>
          <div class="form__group">
            <label>Guest Type</label>
            <input type="text" id="guestTypeDisplay" placeholder="-" readonly />
          </div>
          <div class="form__group">
            <label>Guest Type Discount</label>
            <input type="text" id="guestTypeDiscountDisplay" placeholder="0%" readonly />
          </div>
          <div class="form__group">
            <label>Actual Check-Out (Optional)</label>
            <input type="date" name="actualCheckoutDate" />
          </div>
          <div class="form__group">
            <label>Promotion Discounts</label>
            <div class="form__hint">Select one or more active promotions.</div>
            <div class="table-actions" style="flex-wrap: wrap; gap: 10px;">
              <%
                if (!promotionDiscounts.isEmpty()) {
                  for (DiscountDTO discount : promotionDiscounts) {
              %>
              <label style="display:flex; align-items:center; gap:6px;">
                <input type="checkbox" name="discountIds" value="<%= discount.getId() %>" />
                <span><%= discount.getName() %> (<%= discount.getPercent() %>%)</span>
              </label>
              <%
                  }
                } else {
              %>
              <span class="muted">No active promotions.</span>
              <%
                }
              %>
            </div>
          </div>
          <div class="form__group">
            <label>Manual Discount (%)</label>
            <input type="number" name="manualDiscountPercent" min="0" max="100" step="0.01" placeholder="0" />
          </div>
          <button class="btn btn--primary" type="submit">Generate Bill</button>
        </form>
      </div>

      <div class="panel">
        <h2>Find Reservation</h2>
        <form class="form" method="get" action="<%= request.getContextPath() %>/bills">
          <div class="form__group">
            <label>Guest / Reservation / Room</label>
            <input type="text" name="q" placeholder="Guest name, guest ID, room number, reservation no" value="<%= request.getAttribute("searchQuery") != null ? request.getAttribute("searchQuery") : "" %>" />
          </div>
          <button class="btn btn--outline" type="submit">Search</button>
        </form>

        <table class="table">
          <thead>
            <tr>
              <th>Reservation</th>
              <th>Guest</th>
              <th>Type</th>
              <th>Guest Discount</th>
              <th>Room</th>
              <th>Check-In</th>
              <th>Check-Out</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            <%
              List<ReservationDTO> reservations = (List<ReservationDTO>) request.getAttribute("billingReservations");
              if (reservations != null && !reservations.isEmpty()) {
                for (ReservationDTO reservation : reservations) {
                  DiscountDTO guestDiscount = reservation.getGuestType() == null ? null : guestTypeDiscounts.get(reservation.getGuestType());
                  String guestDiscountValue = guestDiscount == null ? "0" : guestDiscount.getPercent();
            %>
            <tr>
              <td><%= reservation.getReservationNo() %></td>
              <td><%= reservation.getGuestName() %></td>
              <td><%= reservation.getGuestType() == null ? "-" : reservation.getGuestType() %></td>
              <td><%= guestDiscountValue %>%</td>
              <td><%= reservation.getRoomNumber() %></td>
              <td><%= reservation.getCheckInDate() %></td>
              <td><%= reservation.getCheckOutDate() %></td>
              <td>
                <button class="btn btn--outline btn--sm" type="button"
                        data-reservation-id="<%= reservation.getId() %>"
                        data-guest-name="<%= reservation.getGuestName() %>"
                        data-guest-type="<%= reservation.getGuestType() == null ? "" : reservation.getGuestType() %>"
                        data-guest-discount="<%= guestDiscountValue %>">
                  Select
                </button>
              </td>
            </tr>
            <%
                }
              } else if (request.getAttribute("searchQuery") != null) {
            %>
            <tr>
              <td colspan="8">No reservations found.</td>
            </tr>
            <%
              }
            %>
          </tbody>
        </table>
      </div>
    </div>

    <%
      BillDTO bill = (BillDTO) session.getAttribute("lastBill");
      if (bill != null) {
    %>
    <div class="section panel">
      <h2>Latest Bill</h2>
      <table class="table">
        <tr><th>Bill No</th><td><%= bill.getBillNo() %></td></tr>
        <tr><th>Reservation</th><td><%= bill.getReservationNo() %></td></tr>
        <tr><th>Guest</th><td><%= bill.getGuestName() %></td></tr>
        <tr><th>Room</th><td><%= bill.getRoomNumber() %></td></tr>
        <tr><th>Nights</th><td><%= bill.getNumberOfNights() %></td></tr>
        <tr><th>Total</th><td><%= bill.getTotalAmount() %></td></tr>
        <tr><th>Discount</th><td><%= bill.getDiscountAmount() %></td></tr>
        <tr><th>Tax</th><td><%= bill.getTaxAmount() %></td></tr>
        <tr><th>Net Amount</th><td><%= bill.getNetAmount() %></td></tr>
      </table>
    </div>
    <%
      }
    %>
  </main>
  <script>
    document.addEventListener("DOMContentLoaded", function () {
      var reservationInput = document.getElementById("reservationIdInput");
      var guestNameDisplay = document.getElementById("guestNameDisplay");
      var guestTypeDisplay = document.getElementById("guestTypeDisplay");
      var guestTypeDiscountDisplay = document.getElementById("guestTypeDiscountDisplay");
      var selectButtons = document.querySelectorAll("[data-reservation-id]");
      selectButtons.forEach(function (button) {
        button.addEventListener("click", function () {
          var reservationId = button.getAttribute("data-reservation-id");
          var guestName = button.getAttribute("data-guest-name");
          var guestType = button.getAttribute("data-guest-type");
          var guestDiscount = button.getAttribute("data-guest-discount");
          if (reservationInput) {
            reservationInput.value = reservationId || "";
          }
          if (guestNameDisplay) {
            guestNameDisplay.value = guestName || "";
          }
          if (guestTypeDisplay) {
            guestTypeDisplay.value = guestType || "-";
          }
          if (guestTypeDiscountDisplay) {
            guestTypeDiscountDisplay.value = (guestDiscount || "0") + "%";
          }
          if (reservationInput) {
            reservationInput.focus();
          }
        });
      });
    });
  </script>
  <%@ include file="/WEB-INF/partials/footer.jspf" %>
</body>
</html>

