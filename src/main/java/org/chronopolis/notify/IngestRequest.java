/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.chronopolis.notify;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

/**
 *
 * @author toaster
 */
public class IngestRequest {

    private static final Logger LOG = Logger.getLogger(IngestRequest.class);
    private Set<String> seenfiles = new HashSet<String>();
    private boolean read = false;
    private List<String> errors = new ArrayList<String>();
    private String space = null;
    private String account = null;
    private File manifestFile;

    public IngestRequest() {
    }

    public IngestRequest(String account, String space) {
        this.space = space;
        this.account = account;
    }

    public File getManifestFile() {
        return manifestFile;
    }

    
    
    public String getAccount() {
        return account;
    }

    public String getSpace() {
        return space;
    }

    /**
     * List all errors encountered during manifest parsing
     * 
     * @return 
     */
    public List<String> getErrors() {
        if (read) {
            return Collections.unmodifiableList(errors);
        } else {
            throw new IllegalStateException("Attempt to read errors prior to reading stream");
        }

    }

    public boolean hasErrors() {
        if (read) {
            return !errors.isEmpty();
        } else {
            throw new IllegalStateException("Attempt to read errors prior to reading stream");
        }
    }

    /**
     * Read bagit-formed input stream, return calculated digest on stream. THis will 
     * always close the supplied inputstream after reading.
     * 
     * @param is
     * @param digest digest algorithm to use to check input stream
     * @return calculated digest
     * @throws IllegalStateException if manifest has already been read
     */
    public String readStream(InputStream is, MessageDigest digest) throws IOException {

        if (read) {
            is.close();
            throw new IllegalStateException("Manifest has already been read!");
        }
        manifestFile = File.createTempFile("manifest", "tmp", ManifestDirectoryListener.getDirectory());
        BufferedWriter bw = new BufferedWriter(new FileWriter(manifestFile));
        read = true;

        digest.reset();
        DigestInputStream dis = new DigestInputStream(is, digest);
        BufferedReader br = new BufferedReader(new InputStreamReader(dis));

        int lines = 0;
        String line;

        try {
            while ((line = br.readLine()) != null) {
                LOG.trace("Reading input line: " + line);
                if (loadLine(line))
                {
                    // Write line to manifest file;
                    bw.write(line);
                    bw.newLine();
                }
                lines++;
            }

        } catch (IOException e) {
            LOG.error("Error reading manifest stream ", e);
            errors.add("Error reading: " + e.getLocalizedMessage());
            bw.close();
            manifestFile.delete();
            throw e;
        } finally {
            br.close();
            bw.close();
        }

        LOG.trace("Read " + lines + " total lines");


        byte[] messageDigest = digest.digest();
        String digestString = new String(Hex.encodeHex(messageDigest));
        LOG.trace("Manifest file digest " + digest.getAlgorithm() + " " + digestString);
        return digestString;

    }

    private boolean loadLine(String line) {
        if (line.isEmpty()) {
            LOG.error("Ignoring Empty Line");
            errors.add("Ignoring Empty Line");
            return false;
        }

        String[] parts = line.split("\\s+", 2);
        if (parts == null || parts.length != 2) {
            LOG.error("Ignoring Bad Line: " + line);
            errors.add("Ignoring Bad Line: " + line);
            return false;
        }

        parts[0].trim();
        parts[1].trim();

        if (parts[0].isEmpty() || parts[1].isEmpty()) {
            LOG.error("Ignoring Bad Line: " + line);
            errors.add("Ignoring Bad Line: " + line);
            return false;
        }

        if (seenfiles.contains(parts[1])) {
            String error = "Duplicate manifest file: " + parts[1] + " not replacing ";

            errors.add(error);
            LOG.info(error);
            return false;
        }

        seenfiles.add(parts[1]);
        return true;
    }
}
