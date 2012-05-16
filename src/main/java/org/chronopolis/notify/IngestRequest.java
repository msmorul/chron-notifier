/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.chronopolis.notify;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

/**
 *
 * @author toaster
 */
public class IngestRequest {

    private static final Logger LOG = Logger.getLogger(IngestRequest.class);
    private Map<String, String> manifest;
    private List<String> errors = new ArrayList<String>();
    private String space = null;
    private String account = null;

    public IngestRequest() {
    }
    
    public IngestRequest(String account, String space) {
        this.space = space;
        this.account = account;
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
        if (manifest != null) {
            return Collections.unmodifiableList(errors);
        } else {
            throw new IllegalStateException("Attempt to read errors prior to reading stream");
        }

    }

    public boolean hasErrors() {
        if (manifest != null) {
            return !errors.isEmpty();
        } else {
            throw new IllegalStateException("Attempt to read errors prior to reading stream");
        }
    }

    /**
     * Return stored manifest, only use after readStream has been called.
     * 
     * @return stored manifest
     */
    public Map<String, String> getManifest() {
        if (manifest != null) {
            return Collections.unmodifiableMap(manifest);
        } else {
            throw new IllegalStateException("Attempt to read manifest prior to reading stream");
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

        if (manifest != null) {
            is.close();
            throw new IllegalStateException("Manifest has already been read!");
        }
        manifest = new HashMap<String, String>();

        digest.reset();
        DigestInputStream dis = new DigestInputStream(is, digest);
        BufferedReader br = new BufferedReader(new InputStreamReader(dis));

        int lines = 0;
        String line;

        try {
            while ((line = br.readLine()) != null) {
                LOG.trace("Reading input line: " + line);
                loadLine(line);
                lines++;
            }

        } catch (IOException e) {
            LOG.error("Error reading manifest stream ", e);
            manifest = null;
            throw e;
        } finally {
            br.close();
        }

        LOG.trace("Read " + lines + " total lines");


        byte[] messageDigest = digest.digest();
        String digestString = new String(Hex.encodeHex(messageDigest));
        LOG.trace("Manifest file digest " + digest.getAlgorithm() + " " + digestString);
        return digestString;

    }

    private void loadLine(String line) {
        if (line.isEmpty()) {
            return;
        }

        String[] parts = line.split("\\s+", 2);
        if (parts == null || parts.length != 2) {
            LOG.error("Ignoring Bad Line: " + line);
            errors.add("Ignoring Bad Line: " + line);
            return;
        }

        parts[0].trim();
        parts[1].trim();

        if (parts[0].isEmpty() || parts[1].isEmpty()) {
            LOG.error("Ignoring Bad Line: " + line);
            errors.add("Ignoring Bad Line: " + line);
            return;
        }

        if (manifest.containsKey(parts[1])) {
            String error = "Duplicate manifest key: " + parts[1]
                    + " existing digest " + manifest.get(parts[1]) + " new " + parts[0] + " not replacing";

            errors.add(line);
            LOG.info(error);
            return;
        }

        manifest.put(parts[1], parts[0]);
    }
}
