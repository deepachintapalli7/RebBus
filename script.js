function searchBus() {

    let source = document.getElementById("source").value;
    let destination = document.getElementById("destination").value;

    if (source === "" || destination === "") {
        alert("Please enter source and destination!");
        return;
    }

    document.getElementById("busList").innerHTML =
        "<p>🚌 101 - Orange Travels | Hyderabad → Bangalore | 40 Seats</p>" +
        "<p>🚌 102 - VRL Travels | Chennai → Hyderabad | 45 Seats</p>" +
        "<p>🚌 103 - SRS Travels | Bangalore → Chennai | 35 Seats</p>";
}


function selectSeat(seat) {

    let seats = document.querySelectorAll(".seat");

    seats.forEach(function(s) {
        s.classList.remove("selected");
    });

    seat.classList.add("selected");

    document.getElementById("selectedSeat").innerText = seat.innerText;
}


function bookTicket() {

    let name = document.getElementById("name").value;
    let age = document.getElementById("age").value;
    let gender = document.getElementById("gender").value;
    let phone = document.getElementById("phone").value;
    let email = document.getElementById("email").value;
    let seat = document.getElementById("selectedSeat").innerText;

    let journeyDate = document.getElementById("journeyDate").value;
    let busNumber = 101;

    if (name === "" || age === "" || gender === "" ||
        phone === "" || email === "") {

        alert("Please fill all customer details!");
        return;
    }

    if (!/^[6-9][0-9]{9}$/.test(phone)) {

        alert("Invalid 10-digit phone number!");
        return;
    }

if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
        alert("Invalid email!");
        return;
    }

    if (seat === "None") {

        alert("Please select a seat!");
        return;
    }

    fetch("/book", {

        method: "POST",

        headers: {
            "Content-Type": "application/json"
        },

        body: JSON.stringify({
            name: name,
            age: age,
            gender: gender,
            phone: phone,
            email: email,
            seat: seat,
            journeyDate: journeyDate,
			busNumber: String(busNumber)
        })
    })

    .then(response => response.text())

    .then(data => {

        document.getElementById("message").innerText = data;

    })

    .catch(error => {

        document.getElementById("message").innerText =
            "Booking failed!";

        console.log(error);

    });
}
