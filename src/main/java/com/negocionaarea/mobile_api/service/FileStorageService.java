package com.negocionaarea.mobile_api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
public class FileStorageService {

    private final Path rootDir;

    public FileStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.rootDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.rootDir);
        } catch (IOException e) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Falha ao criar diretorio de upload", e);
        }
    }

    public StoredFile storeProdutoImagem(UUID produtoId, MultipartFile imagem) {
        if (imagem == null || imagem.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Arquivo 'imagem' e obrigatorio");
        }

        String contentType = imagem.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new ResponseStatusException(BAD_REQUEST, "Arquivo enviado não é uma imagem (content-type invalido)");
        }

        String originalName = StringUtils.cleanPath(imagem.getOriginalFilename() == null ? "" : imagem.getOriginalFilename());
        String ext = extensionFrom(contentType, originalName);

        String fileName = UUID.randomUUID() + ext;
        Path produtoDir = rootDir.resolve("produtos").resolve(produtoId.toString()).normalize();
        Path target = produtoDir.resolve(fileName).normalize();


        if (!target.startsWith(produtoDir)) {
            throw new ResponseStatusException(BAD_REQUEST, "Nome de arquivo inválido");
        }

        try {
            Files.createDirectories(produtoDir);
            Files.copy(imagem.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Falha ao salvar imagem", e);
        }

        String publicPath = "/uploads/produtos/" + produtoId + "/" + fileName;
        return new StoredFile(publicPath, contentType);
    }

    public void tryDeleteIfUnderUploads(String publicPath) {
        if (publicPath == null || publicPath.isBlank()) return;
        if (!publicPath.startsWith("/uploads/")) return;


        String relative = publicPath.substring("/uploads/".length());
        Path target = rootDir.resolve(relative).normalize();
        if (!target.startsWith(rootDir)) return;

        try {
            Files.deleteIfExists(target);
        } catch (IOException ignored) {
            // Best-effort cleanup only.
        }
    }

    public StoredFile storeEmpresaLogo(UUID empresaId, MultipartFile logo) {
        if (logo == null || logo.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Arquivo 'logo' e obrigatorio");
        }

        String contentType = logo.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new ResponseStatusException(BAD_REQUEST, "Arquivo enviado não é uma imagem");
        }

        String originalName = StringUtils.cleanPath(logo.getOriginalFilename() == null ? "" : logo.getOriginalFilename());
        String ext = extensionFrom(contentType, originalName);
        String fileName = UUID.randomUUID() + ext;

        Path empresaDir = rootDir.resolve("empresas").resolve(empresaId.toString()).normalize();
        Path target = empresaDir.resolve(fileName).normalize();

        if (!target.startsWith(empresaDir)) {
            throw new ResponseStatusException(BAD_REQUEST, "Nome de arquivo inválido");
        }

        try {
            Files.createDirectories(empresaDir);
            Files.copy(logo.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Falha ao salvar logo", e);
        }

        String publicPath = "/uploads/empresas/" + empresaId + "/" + fileName;
        return new StoredFile(publicPath, contentType);
    }

    private static String extensionFrom(String contentType, String originalName) {

        String ext = switch (contentType.toLowerCase(Locale.ROOT)) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            default -> "";
        };
        if (!ext.isBlank()) return ext;

        int idx = originalName.lastIndexOf('.');
        if (idx > -1 && idx < originalName.length() - 1) {
            String candidate = originalName.substring(idx).toLowerCase(Locale.ROOT);

            if (candidate.equals(".jpg") || candidate.equals(".jpeg")) return ".jpg";
            if (candidate.equals(".png")) return ".png";
            if (candidate.equals(".webp")) return ".webp";
            if (candidate.equals(".gif")) return ".gif";
        }
        return "";
    }

    public record StoredFile(String publicPath, String contentType) {
    }
}

