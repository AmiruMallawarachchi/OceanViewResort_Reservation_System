<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Billing | Reservationist</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css" />
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

    <div class="panel">
      <form class="form">
        <div class="form__group">
          <label>Reservation Number</label>
          <input type="text" placeholder="RES-2026-001" />
        </div>
        <div class="form__group">
          <label>Apply Discount (%)</label>
          <input type="number" placeholder="0" />
        </div>
        <button class="btn btn--primary" type="submit">Generate Bill</button>
      </form>
    </div>
  </main>
  <%@ include file="/WEB-INF/partials/footer.jspf" %>
</body>
</html>
