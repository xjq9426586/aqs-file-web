package com.std.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/file/*")
public class FileController {
	@Value("${resourceLocations.uploadPath}")
	private String uploadPath;
	@Value("${resourceHandler.pathHandler}")
	private String pathHandler;
	private String enc = "utf-8";
	@PostMapping("upload")
	public Map<String, Object> upload(@RequestParam Map<String, Object> params, @RequestParam("file") MultipartFile file) {
		Map<String, Object> result = new HashMap<>();
		result.put("size", file.getSize());
		String path = String.valueOf(params.get("path"));
		String fileName = file.getOriginalFilename();
		result.put("exName", fileName.substring(fileName.lastIndexOf(".") + 1));
		File f = new File(!path.equals("null") ? uploadPath + path : uploadPath);
		if (!f.exists()) {
			f.mkdirs();
		}
		File taget = new File(f, fileName);
		result.put("url", taget.getPath());
		try {
			if (!taget.exists()) {
				taget.createNewFile();
			}
			file.transferTo(taget.getAbsoluteFile());

		} catch (IOException e) {
			e.printStackTrace();
			result.put("error", e.getMessage());
			return result;
		}

		return result;

	}

    @PostMapping("bathUpload")
    public Map<String, Object> bathUpload(@RequestParam Map<String, Object> params, @RequestParam("file") List<MultipartFile> files) {
        Map<String, Object> result = new HashMap<>();

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
			String path = String.valueOf(params.get("path"));
			File f = new File(!path.equals("null") ? uploadPath + path : uploadPath);
			if (!f.exists()) {
				f.mkdirs();
			}
            File taget = new File(f, fileName);
            try {
                if (!taget.exists()) {
                    taget.createNewFile();
                }
                file.transferTo(taget.getAbsoluteFile());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return result;

    }

	@GetMapping(value = "/download")
	public void download(HttpServletRequest request, HttpServletResponse res) {
		BufferedInputStream bis = null;
		OutputStream os = null;
		try {
			String path = request.getQueryString();
            path = URLDecoder.decode(path, "utf-8");
			String fileName = path.substring(path.lastIndexOf("/") + 1);
            String agent = request.getHeader("User-Agent").toUpperCase();
            if ((agent.indexOf("MSIE") > 0) || ((agent.indexOf("GECKO") > 0) && (agent.indexOf("RV:11") > 0))) {
                fileName = URLEncoder.encode(fileName, this.enc);
            } else {
                fileName = new String(fileName.replaceAll(" ", "").getBytes(this.enc), "ISO8859-1");
            }
			res.setHeader("content-type", "application/octet-stream");
			res.setContentType("application/octet-stream");
			res.setHeader("Content-Disposition", "attachment;filename=" + fileName);

			File file = new File(path);
			byte[] buff = new byte[1024];
			os = res.getOutputStream();
			bis = new BufferedInputStream(new FileInputStream(file));
			int i = bis.read(buff);
			while (i != -1) {
				os.write(buff, 0, buff.length);
				os.flush();
				i = bis.read(buff);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


}
