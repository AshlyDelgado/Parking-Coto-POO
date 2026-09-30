package cr.ac.una.parking.coto;

import cr.ac.una.parking.coto.enums.PaymentType;
import cr.ac.una.parking.coto.enums.SpaceType;
import cr.ac.una.parking.coto.model.Car;
import cr.ac.una.parking.coto.model.ParkingSpace;
import cr.ac.una.parking.coto.model.ParkingTicket;
import java.time.LocalDateTime;

public class ParkingCoto {

    public static void main(String[] args) {
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.registerSpace(new ParkingSpace(1, SpaceType.CAR));
        parkingLot.registerSpace(new ParkingSpace(2, SpaceType.MOTORCYCLE));
        parkingLot.registerSpace(new ParkingSpace(3, SpaceType.FREIGHT));

        Car car = new Car("B123456", "Toyota", "Corolla", "Blanco");
        parkingLot.registerVehicle(car);

        ParkingTicket ticket = parkingLot.registerEntry(car, LocalDateTime.of(2026, 9, 30, 8, 0));
        ticket.close(LocalDateTime.of(2026, 9, 30, 10, 0));
        parkingLot.registerPayment(ticket, PaymentType.CASH, LocalDateTime.of(2026, 9, 30, 10, 5));

        System.out.println("Ticket #" + ticket.getId());
        System.out.println("Monto: ₡" + ticket.getAmount());
        System.out.println("Ingreso total: ₡" + parkingLot.getTotalIncome());
    }
}
