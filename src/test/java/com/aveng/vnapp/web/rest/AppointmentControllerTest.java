package com.aveng.vnapp.web.rest;

import static com.aveng.vnapp.domain.enums.AppointmentState.CANCELLED;
import static com.aveng.vnapp.domain.enums.AppointmentState.FINALIZED;
import static com.aveng.vnapp.domain.enums.AppointmentState.RESERVED;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import com.aveng.vnapp.VnappApplication;
import com.aveng.vnapp.domain.DoctorEntity;
import com.aveng.vnapp.domain.PatientEntity;
import com.aveng.vnapp.domain.TransactionEntity;
import com.aveng.vnapp.domain.enums.TransactionType;
import com.aveng.vnapp.repository.AppointmentRepository;
import com.aveng.vnapp.repository.DoctorRepository;
import com.aveng.vnapp.repository.PatientRepository;
import com.aveng.vnapp.repository.TransactionRepository;
import com.aveng.vnapp.service.dto.AppointmentDTO;

import io.restassured.RestAssured;
import io.restassured.config.DecoderConfig;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = VnappApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AppointmentControllerTest {

    public static final String APPOINTMENTS_PATH = "/appointments";
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @LocalServerPort
    private int port;

    private PatientEntity patientEntity;
    private DoctorEntity doctorEntity;

    @Before
    public void setUp() {
        cleanDB();

    }

    @After
    public void tearDown() {
        cleanDB();
    }

    @Test
    public void reserve_success() {
        int duration = 60;

        OffsetDateTime start =
            OffsetDateTime.now(ZoneOffset.UTC).plus(2, ChronoUnit.HOURS).truncatedTo(ChronoUnit.MINUTES);
        OffsetDateTime end = start.plus(duration, ChronoUnit.MINUTES).truncatedTo(ChronoUnit.MINUTES);

        initDocAndPatient();

        AppointmentDTO requestDto = AppointmentDTO.builder()
            .startDate(start)
            .duration(duration)
            .patientId(patientEntity.getId())
            .doctorId(doctorEntity.getId())
            .build();

        AppointmentDTO response = given().contentType(MediaType.APPLICATION_JSON)
            .config(RestAssured.config()
                .decoderConfig(DecoderConfig.decoderConfig().contentDecoders(DecoderConfig.ContentDecoder.DEFLATE)))
            .when()
            .port(port)
            .body(requestDto)
            .post(APPOINTMENTS_PATH)
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .as(AppointmentDTO.class);

        assertEquals(response.getDoctorId(), doctorEntity.getId());
        assertEquals(response.getPatientId(), patientEntity.getId());
        assertEquals(response.getStartDate(), start);
        assertEquals(response.getEndDate(), end);
        assertEquals(response.getState(), RESERVED);
    }

    @Test
    public void finalize_success() {
        AppointmentDTO response = reserveAppointment(false);

        AppointmentDTO finalizeResponse = given().contentType(MediaType.APPLICATION_JSON)
            .config(RestAssured.config()
                .decoderConfig(DecoderConfig.decoderConfig().contentDecoders(DecoderConfig.ContentDecoder.DEFLATE)))
            .when()
            .port(port)
            .post(APPOINTMENTS_PATH + "/" + response.getId() + "/finalize")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .as(AppointmentDTO.class);

        assertEquals(finalizeResponse.getId(), response.getId());
        assertEquals(finalizeResponse.getState(), FINALIZED);

        String finalizeTransactionId = finalizeResponse.getTransactions().get(0);

        TransactionEntity finalizeTransaction = transactionRepository.findById(finalizeTransactionId).get();

        assertEquals(finalizeTransaction.getType(), TransactionType.FINALIZATION);
        assertEquals(finalizeTransaction.getAppointmentId(), finalizeResponse.getId());
        assertEquals(finalizeTransaction.getPatientId(), patientEntity.getId());
        assertEquals(finalizeTransaction.getCost(), BigDecimal.TEN.setScale(2));
    }

    @Test
    public void cancel_success() {
        AppointmentDTO finalizedAppointment = finalizeAppointment(true);

        AppointmentDTO cancelResponse = given().contentType(MediaType.APPLICATION_JSON)
            .config(RestAssured.config()
                .decoderConfig(DecoderConfig.decoderConfig().contentDecoders(DecoderConfig.ContentDecoder.DEFLATE)))
            .when()
            .port(port)
            .post(APPOINTMENTS_PATH + "/" + finalizedAppointment.getId() + "/cancel")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .as(AppointmentDTO.class);

        assertEquals(cancelResponse.getId(), finalizedAppointment.getId());
        assertEquals(cancelResponse.getState(), CANCELLED);

        String finalizeTransactionId = cancelResponse.getTransactions()
            .stream()
            .filter(transId -> transactionRepository.findById(transId)
                .map(transactionEntity -> transactionEntity.getType().equals(TransactionType.CANCELLATION))
                .orElse(false))
            .collect(Collectors.toList())
            .get(0);

        TransactionEntity finalizeTransaction = transactionRepository.findById(finalizeTransactionId).get();

        assertEquals(finalizeTransaction.getType(), TransactionType.CANCELLATION);
        assertEquals(finalizeTransaction.getAppointmentId(), cancelResponse.getId());
        assertEquals(finalizeTransaction.getPatientId(), patientEntity.getId());
        assertEquals(finalizeTransaction.getCost(), BigDecimal.valueOf(2.50).setScale(2));
    }

    private AppointmentDTO reserveAppointment(boolean isLateAppointment) {
        int duration = 60;

        OffsetDateTime start = isLateAppointment ?
            OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES) :
            OffsetDateTime.now(ZoneOffset.UTC).plus(2, ChronoUnit.HOURS).truncatedTo(ChronoUnit.MINUTES);

        initDocAndPatient();

        AppointmentDTO requestDto = AppointmentDTO.builder()
            .startDate(start)
            .duration(duration)
            .patientId(patientEntity.getId())
            .doctorId(doctorEntity.getId())
            .build();

        return given().contentType(MediaType.APPLICATION_JSON)
            .config(RestAssured.config()
                .decoderConfig(DecoderConfig.decoderConfig().contentDecoders(DecoderConfig.ContentDecoder.DEFLATE)))
            .when()
            .port(port)
            .body(requestDto)
            .post(APPOINTMENTS_PATH)
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .as(AppointmentDTO.class);
    }

    private AppointmentDTO finalizeAppointment(boolean isLateAppointment) {
        AppointmentDTO response = reserveAppointment(isLateAppointment);

        return given().contentType(MediaType.APPLICATION_JSON)
            .config(RestAssured.config()
                .decoderConfig(DecoderConfig.decoderConfig().contentDecoders(DecoderConfig.ContentDecoder.DEFLATE)))
            .when()
            .port(port)
            .post(APPOINTMENTS_PATH + "/" + response.getId() + "/finalize")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .as(AppointmentDTO.class);
    }

    private void initDocAndPatient() {
        patientEntity = patientRepository.save(PatientEntity.builder().firstName("ossm").lastName("hasta").build());

        doctorEntity = doctorRepository.save(
            DoctorEntity.builder().firstName("doc").lastName("whatsapp").hourlyRate(BigDecimal.TEN).build());
    }

    private void cleanDB() {
        transactionRepository.deleteAll();
        appointmentRepository.deleteAll();
        doctorRepository.deleteAll();
        patientRepository.deleteAll();
    }

}