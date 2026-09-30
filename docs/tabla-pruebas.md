# Tabla de pruebas del proyecto Parking Coto

| # | Caso de prueba | Entrada | Resultado esperado | Resultado obtenido |
|---|---|---|---|---|
| 1 | Ingreso correcto de un automóvil | Auto registrado y espacio de carro disponible | Ticket activo generado | Correcto |
| 2 | Ingreso correcto de una motocicleta | Moto registrada y espacio compatible | Ticket activo generado | Correcto |
| 3 | Ingreso correcto de un vehículo de carga | Vehículo de carga registrado | Ticket activo generado | Correcto |
| 4 | Espacio ocupado | Dos vehículos intentan usar el mismo espacio | Se rechaza la asignación | Correcto |
| 5 | Espacio fuera de servicio | Se intenta usar un espacio marcado fuera de servicio | Se rechaza la asignación | Correcto |
| 6 | Espacio incompatible | Auto intenta usar espacio de moto | Se rechaza la asignación | Correcto |
| 7 | Vehículo con ticket activo duplicado | Mismo vehículo intenta entrar otra vez | Se rechaza la operación | Correcto |
| 8 | Permanencia de 1 minuto | 1 minuto de permanencia | Cobro equivalente a 1 hora | Correcto |
| 9 | Permanencia de 60 minutos | 60 minutos de permanencia | Cobro equivalente a 1 hora | Correcto |
| 10 | Permanencia de 61 minutos | 61 minutos de permanencia | Cobro equivalente a 2 horas | Correcto |
| 11 | Más de 10 horas con tarifa máxima | 10 o más horas | Aplicación del tope diario | Correcto |
| 12 | Cierre correcto del ticket | Salida posterior a la entrada | Ticket pasa a cerrado y libera espacio | Correcto |
| 13 | Pago correcto | Se registra el pago de un ticket cerrado | Ticket pasa a pagado y se acumula ingreso | Correcto |
| 14 | Liberación del espacio | Finaliza la salida correctamente | Espacio vuelve a disponible | Correcto |
| 15 | Cálculo de ingresos totales | Pago realizado | Total acumulado correcto | Correcto |
| 16 | Consulta de vehículos dentro | Vehículo activo o cerrado recientemente | Lista con vehículos dentro | Correcto |

## Observaciones
- La regla de fracción de hora se cobra como hora completa.
- La tarifa máxima diaria se aplica cuando la permanencia iguala o supera las 10 horas cobradas.
- El espacio solo se libera al cerrar el ticket correctamente.
