<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Manage Rooms</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css" />
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

    <div class="grid" style="grid-template-columns: 1fr 1fr; gap: 20px;">
      <div class="panel">
        <h2>Add Room Type</h2>
        <form class="form">
          <div class="form__group">
            <label>Type Name</label>
            <input type="text" placeholder="Deluxe" />
          </div>
          <div class="form__group">
            <label>Rate Per Night</label>
            <input type="number" placeholder="15000" />
          </div>
          <div class="form__group">
            <label>Amenities</label>
            <input type="text" placeholder="WiFi, AC, Balcony" />
          </div>
          <button class="btn btn--primary" type="submit">Save Type</button>
        </form>
      </div>

      <div class="panel">
        <h2>Add Room</h2>
        <form class="form">
          <div class="form__group">
            <label>Room Number</label>
            <input type="text" placeholder="101" />
          </div>
          <div class="form__group">
            <label>Room Type</label>
            <select>
              <option>Standard</option>
              <option>Deluxe</option>
              <option>Suite</option>
            </select>
          </div>
          <button class="btn btn--primary" type="submit">Add Room</button>
        </form>
      </div>
    </div>
  </main>
  <%@ include file="/WEB-INF/partials/footer.jspf" %>
</body>
</html>
