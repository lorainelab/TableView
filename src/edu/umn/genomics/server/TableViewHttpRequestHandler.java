package edu.umn.genomics.server;

import edu.umn.genomics.table.FileTableModel;
import edu.umn.genomics.table.LoadTable;
import edu.umn.genomics.table.TableView;
import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

public class TableViewHttpRequestHandler implements Runnable {
    private static final Logger ourLogger = Logger.getLogger(TableViewHttpRequestHandler.class.getPackage().getName());
    private static final String UTF8 = "UTF-8";
    
    private final Socket socket;
    private final TableView tv;
    
    public TableViewHttpRequestHandler(TableView tv, Socket socket) {
        this.socket = socket;
        this.tv = tv;
    }

    @Override
    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processRequest() throws IOException {
        BufferedReader reader = null;
        OutputStream output = null;

        String line;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = socket.getOutputStream();
            while ((line = reader.readLine()) != null && line.trim().length() > 0) {
                String command = null;
                if (line.length() >= 4 && line.substring(0, 4).toUpperCase().equals("GET ")) {
                    String[] getCommand = line.substring(4).split(" ");
                    if (getCommand.length > 0) {
                        command = getCommand[0];
                    }
                }

                if (command != null) {
                    parseAndGoToBookmark(command);

                } else {
                    //Do nothing for now.
                }

            }
            output.write(TableViewServer.http_response.getBytes());
            output.flush();
        } finally {
            if (output != null) {
                output.close();
            }
            if (reader != null) {
                reader.close();
            }
            try {
                socket.close();
            } catch (Exception e) {
                // do nothing
            }
        }

    }

    private void parseAndGoToBookmark(String command) throws NumberFormatException, IOException {
        ourLogger.log(Level.FINE, "Command = {0}", command);
        int index = command.indexOf('?');
        if (index >= 0 && index < command.length()) {
            String params = command.substring(index + 1);
            Map<String, String[]> paramMap = new HashMap<String, String[]>();
            parseParametersFromQuery(paramMap, params, true);
            String[] url = paramMap.get("url");
            FileTableModel ftm = new FileTableModel(url[0]);
            if (ftm != null) {
                tv.setTableModel(ftm, url[0]);
            }
            //Use object over here
        }
    }

    /**
     * Takes the query parameter string from a URL and parses the parameters
     * into a the given map. All entries will be String arrays, as is expected
     * by HttpServletRequest objects. Thus if the query string is x=3&z&y=4&y=5
     * then the resulting Map will have three String[] entries, for x={"3"} and
     * z={""} and y={"4", "5"}. All entries will be Strings.
     *
     * @param use_url_decoding whether or not to apply {@link URLDecoder} to all
     * keys and values.
     */
    private static void parseParametersFromQuery(Map<String, String[]> map, String query, boolean use_url_decoding) {
        if (query == null) {
            return;
        }
        StringTokenizer st = new StringTokenizer(query, "&");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            int ind_1 = token.indexOf('=');

            String key, value;
            if (ind_1 > 0) {
                key = token.substring(0, ind_1);
                value = token.substring(ind_1 + 1);
            } else {
                key = token;
                value = "";
            }

            if (use_url_decoding) {
                try {
                    key = URLDecoder.decode(key, UTF8);
                    value = URLDecoder.decode(value, UTF8);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            addToMap(map, key, value);
        }
    }

    /**
     * Adds a key->value mapping to a map where the key will map to a String
     * array. If the key already has a String[] mapped to it, this method will
     * increase the length of that array. Otherwise it will create a new
     * String[] of length 1.
     *
     * @param map a Map. It is good to use a LinkedHashMap, if you care about
     * the order of the entries, but this is not required.
     * @param key a non-null, non-empty String. If null or empty, it will not be
     * added to the map. (Empty means "String.trim().length()==0" )
     * @param value a String. Null is ok.
     */
    private static void addToMap(Map<String, String[]> map, String key, String value) {
        if (key == null || key.trim().length() == 0) {
            return;
        }
        String[] array = map.get(key);
        if (array == null) {
            String[] new_array = new String[]{value};
            map.put(key, new_array);
        } else {
            String[] new_array = new String[array.length + 1];
            System.arraycopy(array, 0, new_array, 0, array.length);
            new_array[new_array.length - 1] = value;
            map.put(key, new_array);
        }
    }
}
