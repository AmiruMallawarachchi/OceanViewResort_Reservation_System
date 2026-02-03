<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Reports</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css" />
</head>
<body>
  <%@ include file="/WEB-INF/partials/nav.jspf" %>
  <main class="container">
    <div class="page-header">
      <div>
        <h1 class="page-header__title">Management Reports</h1>
        <p class="muted">Generate operational and revenue summaries.</p>
      </div>
    </div>

    <div class="panel">
      <form class="form" style="grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));">
        <div class="form__group">
          <label>Report Type</label>
          <select>
            <option>Occupancy</option>
            <option>Revenue</option>
            <option>Reservation</option>
          </select>
        </div>
        <div class="form__group">
          <label>Start Date</label>
          <input type="date" />
        </div>
        <div class="form__group">
          <label>End Date</label>
          <input type="date" />
        </div>
        <div class="form__group">
          <label>Format</label>
          <select>
            <option>PDF</option>
            <option>Excel</option>
          </select>
        </div>
        <div class="form__group" style="align-self: end;">
          <button class="btn btn--primary" type="submit">Generate</button>
        </div>
      </form>
    </div>
  </main>
  <%@ include file="/WEB-INF/partials/footer.jspf" %>
</body>
</html>
