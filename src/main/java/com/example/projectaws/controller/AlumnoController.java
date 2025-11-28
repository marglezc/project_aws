package com.example.projectaws.controller;

import com.example.projectaws.model.Alumno;
import com.example.projectaws.repository.AlumnoRepository;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/alumnos")
public class AlumnoController {

    @Autowired
    private AlumnoRepository repository;

    @Autowired
    private S3Client s3Client;

    @Autowired
    private SnsClient snsClient;

    @Autowired
    private DynamoDbClient dynamoDbClient;

    // Variables de entorno
    private final String BUCKET_NAME = System.getenv("S3_BUCKET_NAME") != null ? System.getenv("S3_BUCKET_NAME") : "default-bucket";
    private final String SNS_TOPIC_ARN = System.getenv("SNS_TOPIC_ARN");
    private final String DYNAMO_TABLE = System.getenv("DYNAMO_TABLE_NAME");

    @GetMapping
    public List<Alumno> getAllAlumnos() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAlumnoById(@PathVariable int id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<?> createAlumno(@Valid @org.springframework.web.bind.annotation.RequestBody Alumno alumno) {
        if (alumno.getPassword() == null) {
            alumno.setPassword("123456");
        }
        Alumno nuevoAlumno = repository.save(alumno);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoAlumno);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAlumno(@PathVariable int id, @Valid @org.springframework.web.bind.annotation.RequestBody Alumno alumnoDetails) {
        return repository.findById(id).map(alumno -> {
            alumno.setNombres(alumnoDetails.getNombres());
            alumno.setApellidos(alumnoDetails.getApellidos());
            alumno.setMatricula(alumnoDetails.getMatricula());
            alumno.setPromedio(alumnoDetails.getPromedio());
            repository.save(alumno);
            return ResponseEntity.ok(alumno);
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAlumno(@PathVariable int id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Alumno eliminado exitosamente"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Alumno no encontrado"));
    }

    // AWS Features
    @PostMapping("/{id}/fotoPerfil")
    public ResponseEntity<?> uploadProfilePicture(@PathVariable int id, @RequestParam("foto") MultipartFile file) {
        Optional<Alumno> alumnoOpt = repository.findById(id);
        if (alumnoOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Alumno no encontrado"));

        String key = "alumnos/" + id + "/" + file.getOriginalFilename();

        try {
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(BUCKET_NAME)
                            .key(key)
                            .acl("public-read")
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromBytes(file.getBytes()));

            String url = "https://" + BUCKET_NAME + ".s3.amazonaws.com/" + key;

            Alumno alumno = alumnoOpt.get();
            alumno.setFotoPerfilUrl(url);
            repository.save(alumno);

            return ResponseEntity.ok(Map.of("fotoPerfilUrl", url));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/email")
    public ResponseEntity<?> sendEmail(@PathVariable int id) {
        Optional<Alumno> alumnoOpt = repository.findById(id);
        if (alumnoOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Alumno no encontrado"));

        Alumno alumno = alumnoOpt.get();
        String message = "Calificaciones del alumno: " + alumno.getNombres() + " " + alumno.getApellidos() +
                "\nPromedio: " + alumno.getPromedio();

        snsClient.publish(PublishRequest.builder()
                .topicArn(SNS_TOPIC_ARN)
                .message(message)
                .subject("Reporte de Calificaciones")
                .build());

        return ResponseEntity.ok(Map.of("message", "Correo enviado"));
    }

    @PostMapping("/{id}/session/login")
    public ResponseEntity<?> login(@PathVariable int id, @org.springframework.web.bind.annotation.RequestBody Map<String, String> body) {
        Optional<Alumno> alumnoOpt = repository.findById(id);
        if (alumnoOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Alumno no encontrado"));

        Alumno alumno = alumnoOpt.get();
        String passwordInput = body.get("password");

        if (!alumno.getPassword().equals(passwordInput)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Contraseña incorrecta"));
        }

        String sessionString = UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "") +
                UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
        if(sessionString.length() < 128) sessionString += sessionString; // Asegurar longitud
        sessionString = sessionString.substring(0, 128);

        Map<String, AttributeValue> item = new HashMap<>();
        item.put("id", AttributeValue.builder().s(UUID.randomUUID().toString()).build());
        item.put("fecha", AttributeValue.builder().n(String.valueOf(Instant.now().getEpochSecond())).build());
        item.put("alumnoId", AttributeValue.builder().n(String.valueOf(id)).build());
        item.put("active", AttributeValue.builder().bool(true).build());
        item.put("sessionString", AttributeValue.builder().s(sessionString).build());

        dynamoDbClient.putItem(PutItemRequest.builder()
                .tableName(DYNAMO_TABLE)
                .item(item)
                .build());

        return ResponseEntity.ok(Map.of("sessionString", sessionString));
    }


    @PostMapping("/{id}/session/verify")
    public ResponseEntity<?> verifySession(@PathVariable int id, @org.springframework.web.bind.annotation.RequestBody Map<String, String> body) {
        String sessionString = body.get("sessionString");

        ScanResponse response = dynamoDbClient.scan(ScanRequest.builder()
                .tableName(DYNAMO_TABLE)
                .filterExpression("sessionString = :s AND alumnoId = :a")
                .expressionAttributeValues(Map.of(
                        ":s", AttributeValue.builder().s(sessionString).build(),
                        ":a", AttributeValue.builder().n(String.valueOf(id)).build()
                ))
                .build());

        if (response.count() > 0) {
            Map<String, AttributeValue> item = response.items().get(0);
            if (item.get("active").bool()) {
                return ResponseEntity.ok(Map.of("message", "Sesión válida"));
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Sesión inválida"));
    }

    @PostMapping("/{id}/session/logout")
    public ResponseEntity<?> logout(@PathVariable int id, @org.springframework.web.bind.annotation.RequestBody Map<String, String> body) {
        String sessionString = body.get("sessionString");

        ScanResponse response = dynamoDbClient.scan(ScanRequest.builder()
                .tableName(DYNAMO_TABLE)
                .filterExpression("sessionString = :s")
                .expressionAttributeValues(Map.of(":s", AttributeValue.builder().s(sessionString).build()))
                .build());

        if (response.count() > 0) {
            String pk = response.items().get(0).get("id").s();

            dynamoDbClient.updateItem(UpdateItemRequest.builder()
                    .tableName(DYNAMO_TABLE)
                    .key(Map.of("id", AttributeValue.builder().s(pk).build()))
                    .updateExpression("SET active = :val")
                    .expressionAttributeValues(Map.of(":val", AttributeValue.builder().bool(false).build()))
                    .build());

            return ResponseEntity.ok(Map.of("message", "Sesión cerrada"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Sesión no encontrada"));
    }
}