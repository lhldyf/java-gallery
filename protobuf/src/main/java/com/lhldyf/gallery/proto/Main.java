package com.lhldyf.gallery.proto;

import com.google.protobuf.*;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author lhldyf
 * @date 2019-05-27 15:44
 */
public class Main {
    public static void main(String[] args) {

        Test.Movie.Builder movieBuilder = Test.Movie.newBuilder();
        movieBuilder.setName("The Shining");
        movieBuilder.setType(Test.MovieType.ADULT);
        movieBuilder.setReleaseTimeStamp(327859200);

        System.out.println("Dynamic Message Parse by proto file");
        try {
            byte[] buffer3 = new byte[movieBuilder.build().getSerializedSize()];
            CodedOutputStream codedOutputStream3 = CodedOutputStream.newInstance(buffer3);
            try {
                movieBuilder.build().writeTo(codedOutputStream3);
                System.out.println("buffer3:");
                System.out.println(buffer3);
            } catch (IOException e) {
                e.printStackTrace();
            }


            String protocCMD = "E:\\Code\\java-gallery\\protobuf\\src\\main\\resources\\protoc\\protoc-3.6"
                    + ".1-windows-x86_64.exe --descriptor_set_out=cinema.description test.proto --proto_path=E:\\Code"
                    + "\\java-gallery\\protobuf\\src\\main\\resources\\protoc";
            Process process = Runtime.getRuntime().exec(protocCMD);
            process.waitFor();
            int exitValue = process.exitValue();
            if (exitValue != 0) {
                System.out.println("protoc execute failed");
                return;
            }
            Descriptors.Descriptor pbDescritpor = null;
            DescriptorProtos.FileDescriptorSet descriptorSet =
                    DescriptorProtos.FileDescriptorSet.parseFrom(new FileInputStream("./cinema.description"));
            for (DescriptorProtos.FileDescriptorProto fdp : descriptorSet.getFileList()) {
                Descriptors.FileDescriptor fileDescriptor =
                        Descriptors.FileDescriptor.buildFrom(fdp, new Descriptors.FileDescriptor[] {});
                for (Descriptors.Descriptor descriptor : fileDescriptor.getMessageTypes()) {
                    if (descriptor.getName().equals("Movie")) {
                        System.out.println("Movie descriptor found");
                        pbDescritpor = descriptor;
                        break;
                    }
                }
            }
            if (pbDescritpor == null) {
                System.out.println("No matched descriptor");
                return;
            }
            DynamicMessage.Builder pbBuilder = DynamicMessage.newBuilder(pbDescritpor);

            Message pbMessage = pbBuilder.mergeFrom(buffer3).build();
            System.out.println(pbMessage);

        } catch (Exception e) {
            System.out.println("Exception");
            e.printStackTrace();
        }
    }

}
