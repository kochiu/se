package com.sztx.se.common.util.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import com.alibaba.com.caucho.hessian.io.Hessian2Input;
import com.alibaba.com.caucho.hessian.io.Hessian2Output;
import com.pay1pay.framework.session.SessionMetadata;

public class HessianRedisSerializer implements RedisSerializer<SessionMetadata>
{

	@Override
	public byte[] serialize(SessionMetadata t) throws SerializationException
	{
		try
		{

			ByteArrayOutputStream out = null;
			Hessian2Output hessianOutput = null;
			try
			{
				out = new ByteArrayOutputStream();
				hessianOutput = new Hessian2Output(out);
				hessianOutput.init(out);
				hessianOutput.writeObject(t);
				hessianOutput.close();
				return out.toByteArray();
			} finally
			{
				if (hessianOutput != null)
					hessianOutput.close();
				if (out != null)
					out.close();

			}
		} catch (Exception e)
		{
			throw new SerializationException(e.getMessage(), e);
		}
	}

	@Override
	public SessionMetadata deserialize(byte[] bytes) throws SerializationException
	{
		try
		{
			ByteArrayInputStream bis = null;
			Hessian2Input hsi = null;
			try
			{
				bis = new ByteArrayInputStream(bytes);
				hsi = new Hessian2Input(bis);
				Object obj = hsi.readObject();
				hsi.close();
				SessionMetadata metadata = (SessionMetadata) obj;
				return metadata;
			} finally
			{
				if (bis != null)
					bis.close();
				if (hsi != null)
					hsi.close();
			}
		} catch (Exception e)
		{
			throw new SerializationException(e.getMessage(), e);
		}
	}

}
