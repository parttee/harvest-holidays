(async () => {
  const response = await fetch('http://localhost:8080/?year=2021');
  const result = await response.json();
})();
