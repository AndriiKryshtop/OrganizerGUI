package ua.sumdu.j2se.kryshtop.tasks.util;

import ua.sumdu.j2se.kryshtop.tasks.model.Task;
import ua.sumdu.j2se.kryshtop.tasks.model.TaskList;
import ua.sumdu.j2se.kryshtop.tasks.model.exceptions.MinusException;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TaskIO {
    public static final void write(TaskList tasks, OutputStream out) throws IOException{
        try(DataOutputStream dataOutputStream = new DataOutputStream(out)) {
            dataOutputStream.writeInt(tasks.size());

            for (Task task : tasks) {
                dataOutputStream.writeInt(task.getTitle().length());
                dataOutputStream.writeUTF(task.getTitle());
                dataOutputStream.writeBoolean(task.isActive());
                dataOutputStream.writeInt(task.getRepeatInterval());
                if (task.isRepeated() == false) {
                    dataOutputStream.writeLong(task.getTime().getTime());
                } else {
                    dataOutputStream.writeLong(task.getStartTime().getTime());
                    dataOutputStream.writeLong(task.getEndTime().getTime());
                }
            }
        }
    }

    public static final void read(TaskList tasks, InputStream in) throws IOException{
        try(DataInputStream dataInputStream = new DataInputStream(in)) {
            int size = dataInputStream.readInt();
            for (int i = 0; i < size; i++) {
                Task task = new Task("", null);
                dataInputStream.readInt();
                task.setTitle(dataInputStream.readUTF());
                task.setActive(dataInputStream.readBoolean());
                int interval = dataInputStream.readInt();
                if (interval == 0) {
                    task.setTime(new Date(dataInputStream.readLong()));
                } else {
                    try {
                        task.setTime(new Date(dataInputStream.readLong()),
                                new Date(dataInputStream.readLong()), interval);
                    } catch (MinusException e) {
                        throw new IOException("Start, end or interval time is bad.(Probably, data is corrupted)");
                    }
                }
                tasks.add(task);
            }
        }
    }

    public static final void writeBinary(TaskList tasks, File file) throws IOException {
        try(FileOutputStream fileOutputStream = new FileOutputStream(file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)
        )
        {
            write(tasks, bufferedOutputStream);
        }
    }

    public static final void readBinary(TaskList tasks, File file) throws IOException{
        try(FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)
        )
        {
            read(tasks, bufferedInputStream);
        }
    }

    public static final void write(TaskList tasks, Writer out) throws IOException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(out))
        {
            int i = 0;
            for (Task task : tasks) {
                bufferedWriter.append(task.toString());
                bufferedWriter.append(i < tasks.size()-1 ? ";" : ".");
                bufferedWriter.newLine();
                i++;
            }
        }
    }
    public static final void read(TaskList tasks, Reader in) throws Exception {
        try(BufferedReader bufferedReader = new BufferedReader(in))
        {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

            String line ;
            while ((line = bufferedReader.readLine()) != null) {
                String title = line.substring(line.indexOf('\"') + 1, line.lastIndexOf('\"'));

                boolean active = !line.contains("inactive");

                if (line.contains(" at [")) {
                    String dateString = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
                    Date time = format.parse(dateString);
                    Task task = new Task(title, new Date(time.getTime()));
                    task.setActive(active);
                    tasks.add(task);
                }
                else {
                    String startString = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
                    Date startDate = format.parse(startString);

                    line = line.substring(line.indexOf("]") + 1);
                    String endString = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
                    Date endDate = format.parse(endString);

                    line = line.substring(line.indexOf("]") + 1);
                    String intervalString = line.substring(line.indexOf("["), line.indexOf("]") + 1);
                    String[] intervalStringArray = intervalString.split(" ");
                    intervalStringArray[0] = intervalStringArray[0].substring(1);   //to delete "[" from first element of interval
                    Integer interval = 0;

                    String[] constString = {"d", "h", "m", "s"};
                    for(int i = 0; i < intervalStringArray.length; i += 2){
                        for (int j = 0; j < constString.length; j++) {
                            if (intervalStringArray[i+1].substring(0, 1).compareTo(constString[j]) == 0) {
                                switch (j){
                                    case 0:
                                        interval += Integer.parseInt(intervalStringArray[i]) * 86400;
                                        break;
                                    case 1:
                                        interval += Integer.parseInt(intervalStringArray[i]) * 3600;
                                        break;
                                    case 2:
                                        interval += Integer.parseInt(intervalStringArray[i]) * 60;
                                        break;
                                    case 3:
                                        interval += Integer.parseInt(intervalStringArray[i]);
                                        break;
                                }
                                break;
                            }
                        }
                    }
                    Task task = new Task(title, new Date(startDate.getTime()), new Date(endDate.getTime()), interval);
                    task.setActive(active);
                    tasks.add(task);
                }
            }
        }
    }
    public static final void writeText(TaskList tasks, File file) throws IOException {
        try(FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            PrintWriter printWriter = new PrintWriter(bufferedWriter)
        )
        {
            write(tasks, printWriter);
        }
    }
    public static final void readText(TaskList tasks, File file) throws Exception {
        try(FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader)
        )
        {
            read(tasks, bufferedReader);
        }
    }
}
