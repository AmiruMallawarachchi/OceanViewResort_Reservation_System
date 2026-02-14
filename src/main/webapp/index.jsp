<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% String ctx = request.getContextPath(); %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>OceanView Resort | Home</title>
  <link rel="stylesheet" href="<%= ctx %>/assets/css/style.css?v=20260213" />
</head>
<body>
  <%@ include file="/WEB-INF/partials/nav.jspf" %>
  <main>
    <section class="hero hero--full" style="background-image: url('<%= ctx %>/assets/images/hero-resort.jpg');">
      <div class="hero__inner">
        <svg class="hero__wave" viewBox="0 0 48 24" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
          <path d="M4 12c4-4 8-4 12 0s8 4 12 0 8-4 12 0" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          <path d="M0 18c4-4 8-4 12 0s8 4 12 0 8-4 12 0" stroke="currentColor" stroke-width="2" stroke-linecap="round" opacity="0.8"/>
        </svg>
        <div class="hero__brand">OCEANVIEW RESORT</div>
        <h1 class="hero__title">Reservation System</h1>
        <p class="hero__subtitle">Effortless hotel management from check-in to check-out.<br/>Beautifully simple.</p>
        <div class="hero__actions">
          <a class="btn btn--primary" href="<%= ctx %>/login.jsp">Login</a>
          <a class="btn btn--outline" href="<%= ctx %>/help.jsp">Help</a>
        </div>
      </div>
    </section>

    <section class="feature-section">
      <h2 class="feature-section__title">Everything You Need</h2>
      <p class="feature-section__subtitle">Built for modern hospitality teams who demand speed, accuracy, and insight.</p>
      <div class="feature-cards">
        <div class="feature-card">
          <div class="feature-card__icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/></svg>
          </div>
          <h3>Fast Check-Ins</h3>
          <p>Streamlined check-in process that gets your guests to their rooms in minutes, not hours.</p>
        </div>
        <div class="feature-card">
          <div class="feature-card__icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg>
          </div>
          <h3>Accurate Billing</h3>
          <p>Automated billing system ensures every charge is captured correctly with zero discrepancies.</p>
        </div>
        <div class="feature-card">
          <div class="feature-card__icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/></svg>
          </div>
          <h3>Clear Reports</h3>
          <p>Comprehensive dashboards and reports that give you real-time visibility into operations.</p>
        </div>
      </div>
    </section>
  </main>
  <%@ include file="/WEB-INF/partials/footer.jspf" %>
</body>
</html>
