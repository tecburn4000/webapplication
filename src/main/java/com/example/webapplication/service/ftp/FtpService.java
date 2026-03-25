package com.example.webapplication.service.ftp;

import com.example.webapplication.config.ftp.properties.FtpProperties;
import com.example.webapplication.dto.ftp.FtpEntryDto;
import com.example.webapplication.dto.ftp.ListFileRequestDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.integration.ftp.session.FtpRemoteFileTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Service for interacting with an FTP server.
 * <p>
 * This service provides functionality for listing, streaming, and downloading
 * files from a remote FTP server using {@link FtpRemoteFileTemplate}.
 *
 * <p><b>Responsibilities:</b>
 * <ul>
 *     <li>List files and directories on the FTP server</li>
 *     <li>Stream files directly from FTP to output streams</li>
 *     <li>Download files to the local filesystem</li>
 *     <li>Stream files directly to HTTP responses</li>
 *     <li>Create ZIP streams for multiple files</li>
 * </ul>
 *
 * <p><b>Implementation details:</b>
 * <ul>
 *     <li>Uses Spring Integration's {@link FtpRemoteFileTemplate} for FTP operations</li>
 *     <li>Streams data to avoid loading large files into memory</li>
 *     <li>Creates local directories automatically when downloading files</li>
 * </ul>
 *
 * <p><b>Note:</b>
 * Breadcrumb generation has been moved to a dedicated service to ensure
 * separation of concerns.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FtpService {

    private final FtpRemoteFileTemplate ftpRemoteFileTemplate;
    private final FtpProperties ftpProperties;

    /**
     * Lists files and directories for a given remote path.
     * <p>
     * The result is sorted so that directories appear first, followed by files,
     * and then alphabetically by name.
     *
     * @param path the remote FTP directory path
     * @return a list of {@link FtpEntryDto} representing files and directories
     */
    public List<FtpEntryDto> list(String path) {
        return ftpRemoteFileTemplate.execute(session -> {
            FTPFile[] files = session.list(path);
            return Arrays.stream(files)
                    .map(f -> FtpEntryDto.builder()
                            .name(f.getName())
                            .path(path + "/" + f.getName())
                            .directory(f.isDirectory())
                            .size(f.getSize())
                            .modified(f.getTimestamp().toInstant())
                            .build()
                    )
                    .sorted(Comparator
                    .comparing(FtpEntryDto::isDirectory).reversed()
                    .thenComparing(FtpEntryDto::getName))
                    .toList();
        });
    }

    /**
     * Streams a file from the FTP server to the provided output stream.
     * <p>
     * This method is optimized for large files as it avoids loading the entire
     * file into memory.
     *
     * @param remoteFilePath the path of the file on the FTP server
     * @param outputStream   the target output stream
     */
    public void streamFile(String remoteFilePath, OutputStream outputStream) {
        ftpRemoteFileTemplate.execute(session -> {
            session.read(remoteFilePath, outputStream);
            return null;
        });
    }

    /**
     * Checks whether a file exists on the FTP server.
     *
     * @param remoteFile the remote file path
     * @return {@code true} if the file exists, {@code false} otherwise
     */
    public boolean fileExists(String remoteFile) {
        return ftpRemoteFileTemplate.execute(session -> {
            try {
                return session.exists(remoteFile);
            } catch (Exception _) {
                return false;
            }
        });
    }

    /**
     * Downloads a file from the FTP server to the local filesystem.
     * <p>
     * The file is stored in the directory defined by
     * {@link FtpProperties#getLocalDirectory()}.
     * Missing directories are created automatically.
     *
     * @param filePath the remote file path
     * @return the {@link Path} to the downloaded local file
     * @throws IOException if an I/O error occurs during download
     */
    public Path downloadToLocalFile(String filePath) throws IOException {
        Path targetDirectory = Path.of(ftpProperties.getLocalDirectory());
        Files.createDirectories(targetDirectory);
        Path downloadFile = targetDirectory.resolve(filePath);
        try (OutputStream os = Files.newOutputStream(downloadFile)) {
            streamFile(filePath, os);
        }
        return downloadFile;
    }

    /**
     * Streams a file directly from the FTP server to an HTTP response.
     * <p>
     * This is typically used to provide file downloads in a web application.
     *
     * @param filename the remote file path
     * @param response the HTTP response to write the file content to
     */
    public void downloadToBrowser(String filename, HttpServletResponse response) {
        ftpRemoteFileTemplate.execute(session -> {
            try (OutputStream out = response.getOutputStream()) {
                session.read(filename, out); // FTP -> HTTP Stream
                out.flush();
            }
            return null;
        });
    }

    /**
     * Creates a streaming ZIP archive containing multiple files from the FTP server.
     * <p>
     * The ZIP archive is generated on-the-fly and streamed directly to the client,
     * avoiding temporary file creation on the server.
     *
     * @param listFileRequestDto DTO containing the list of file paths to include
     * @return a {@link StreamingResponseBody} for streaming the ZIP archive
     */
    public StreamingResponseBody createZipStream(ListFileRequestDto listFileRequestDto) {
        return outputStream -> {
            try (ZipOutputStream zipOut = new ZipOutputStream(outputStream)) {
                for (String file : listFileRequestDto.getFilePaths()) {
                    zipOut.putNextEntry(new ZipEntry(file));
                    streamFile(file, zipOut);
                    zipOut.closeEntry();
                }
            }
        };
    }
}
