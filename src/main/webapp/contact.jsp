<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Contact | OceanView Resort</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css" />
</head>
<body>
  <%@ include file="/WEB-INF/partials/nav.jspf" %>
  <main class="container">
    <div class="page-header">
      <div>
        <h1 class="page-header__title">Contact Us</h1>
        <p class="muted">We are here to help you 24/7.</p>
      </div>
    </div>

    <div class="grid grid--3">
      <div class="card">
        <h3>Front Desk</h3>
        <p>+94 91 123 4567</p>
        <p>frontdesk@oceanviewresort.com</p>
      </div>
      <div class="card">
        <h3>Reservation Support</h3>
        <p>+94 91 987 6543</p>
        <p>reservations@oceanviewresort.com</p>
      </div>
      <div class="card">
        <h3>Address</h3>
        <p>Ocean View Resort, Galle, Sri Lanka</p>
      </div>
    </div>

    <div class="section panel">
      <h2>Send a Message</h2>
      <form class="form">
        <div class="form__group">
          <label>Full Name</label>
          <input type="text" placeholder="Enter your name" />
        </div>
        <div class="form__group">
          <label>Email</label>
          <input type="email" placeholder="your@email.com" />
        </div>
        <div class="form__group">
          <label>Message</label>
          <textarea placeholder="Tell us how we can help"></textarea>
        </div>
        <button class="btn btn--primary" type="submit">Send Message</button>
      </form>
    </div>
  </main>
  <%@ include file="/WEB-INF/partials/footer.jspf" %>
</body>
</html>
