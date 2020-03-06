package com.metasploit.meterpreter.stdapi;

import java.io.File;

import com.metasploit.meterpreter.Meterpreter;
import com.metasploit.meterpreter.TLVPacket;
import com.metasploit.meterpreter.TLVType;
import com.metasploit.meterpreter.command.Command;

public class stdapi_fs_ls implements Command {

    public int execute(Meterpreter meterpreter, TLVPacket request, TLVPacket response) throws Exception {
        stdapi_fs_stat statCommand = (stdapi_fs_stat) meterpreter.getCommandManager().getCommand("stdapi_fs_stat");
        String pathString = request.getStringValue(TLVType.TLV_TYPE_DIRECTORY_PATH);
        if (pathString.equals("*")) {
            pathString = ".";
        } else if (pathString.contains("*")) {
            if (pathString.endsWith(File.separator + "*")) {
                pathString = pathString.substring(0, pathString.length() - 1);
            }
        }
        File path = Loader.expand(pathString);
        String[] entries = path.list();
        for (int i = 0; i < entries.length; i++) {
            if (entries[i].equals(".") || entries[i].equals(".."))
                continue;
            File f = new File(path, entries[i]);
            response.addOverflow(TLVType.TLV_TYPE_FILE_NAME, entries[i]);
            response.addOverflow(TLVType.TLV_TYPE_FILE_PATH, f.getCanonicalPath());
            response.addOverflow(TLVType.TLV_TYPE_STAT_BUF, statCommand.stat(f));
        }
        return ERROR_SUCCESS;
    }
}
