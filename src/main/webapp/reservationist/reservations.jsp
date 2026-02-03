<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Reservation Management</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css" />
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

    <div class="grid" style="grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 20px;">
      <div class="panel">
        <h2>Add New Reservation</h2>
        <form class="form">
          <div class="form__group">
            <label>Guest Name</label>
            <input type="text" placeholder="Guest full name" />
          </div>
          <div class="form__group">
            <label>Room Type</label>
            <select>
              <option>Standard</option>
              <option>Deluxe</option>
              <option>Suite</option>
            </select>
          </div>
          <div class="form__group">
            <label>Check-In</label>
            <input type="date" />
          </div>
          <div class="form__group">
            <label>Check-Out</label>
            <input type="date" />
          </div>
          <button class="btn btn--primary" type="submit">Save Reservation</button>
        </form>
      </div>

      <div class="panel">
        <h2>Search Reservation</h2>
        <form class="form">
          <div class="form__group">
            <label>Reservation ID / Guest</label>
            <input type="text" placeholder="Enter ID or guest name" />
          </div>
          <button class="btn btn--outline" type="submit">Search</button>
        </form>

        <table class="table">
          <thead>
            <tr>
              <th>Reservation</th>
              <th>Guest</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>RES-2026-001</td>
              <td>Amaya Perera</td>
              <td>Confirmed</td>
            </tr>
            <tr>
              <td>RES-2026-002</td>
              <td>Nimal Silva</td>
              <td>Pending</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </main>
  <%@ include file="/WEB-INF/partials/footer.jspf" %>
</body>
</html>
