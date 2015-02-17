package cz.kubaspatny.opendayapp.bb;

import cz.kubaspatny.opendayapp.bb.validator.EmailFormatValidator;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.*;
import java.util.ArrayList;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 16/2/2015
 * Time: 19:03
 * Copyright 2015 Jakub Spatny
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@ManagedBean(name = "CSVFileUploadBean")
@ViewScoped
public class CSVFileUploadBean implements Serializable {

    private UploadedFile file;
    private ArrayList<String> emails;

    private boolean uploadedEmptyFile = false;

    public boolean isUploadedEmptyFile() {
        return uploadedEmptyFile;
    }

    public void setUploadedEmptyFile(boolean uploadedEmptyFile) {
        this.uploadedEmptyFile = uploadedEmptyFile;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public ArrayList<String> getEmails() {
        return emails;
    }

    public void setEmails(ArrayList<String> emails) {
        this.emails = emails;
    }

    public void addEmail(String email){
        if(emails == null){
            emails = new ArrayList<String>();
        }

        emails.add(email);
    }

    public void removeEmail(String email){
        if(emails != null){
            emails.remove(email);
        }
    }

    public String upload(FileUploadEvent event) {

        setFile(event.getFile());

        if(file != null) {
            BufferedReader br = null;
            String line = "";
            String cvsSplitBy = ",";

            try {

                br = new BufferedReader(new InputStreamReader(file.getInputstream()));
                while ((line = br.readLine()) != null) {
                    String[] splittedEmails = line.split(cvsSplitBy, -1);
                    for(int i = 0; i < splittedEmails.length; i++){
                        String emailAddr = splittedEmails[i].replaceAll("\"","").trim();
                        if(EmailFormatValidator.isEmailFormatOK(emailAddr)){
                            addEmail(emailAddr);
                        }
                    }
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            uploadedEmptyFile = (emails == null || emails.isEmpty());
            RequestContext.getCurrentInstance().addCallbackParam("errorUploading", false);

        } else {
            uploadedEmptyFile = true;
            System.out.println("Uploaded file is null!");
            RequestContext.getCurrentInstance().addCallbackParam("errorUploading", true);
        }

        return "";
    }



}
