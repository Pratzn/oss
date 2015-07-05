package com.github.pratzn.oss.common.aop;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.aspectj.lang.ProceedingJoinPoint;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.LocationAwareLogger;
import org.springframework.util.ClassUtils;

public class StatisticsLogAdvice
{
  private static final LocationAwareLogger LOGGER = (LocationAwareLogger)LoggerFactory.getLogger(StatisticsLogAdvice.class);
  private boolean indentView = true;
  private boolean simpleClassName = true;
  private boolean showArgsAndResultValue = true;
  private int maxLengthOfValue = 1000;
  private String indent = "  ";
  private String mdcIndent = "indent";
  private Set<String> excludeClassSet = new LinkedHashSet<String>();
  
  public Object doExecution(ProceedingJoinPoint pjp)
    throws Throwable
  {
    String proxyClassName = pjp.getThis().getClass().getName();
    String methodName = null;
    if (this.indentView)
    {
      if (this.simpleClassName) {
        methodName = pjp.getSignature().getDeclaringType().getSimpleName() + '.' + pjp.getSignature().getName();
      } else {
        methodName = pjp.getSignature().getDeclaringTypeName() + '.' + pjp.getSignature().getName();
      }
      printBegin(proxyClassName, methodName, pjp);
    }
    StringBuilder buf = new StringBuilder(64);
    buf.append(methodName);
    buf.append("()");
    
    long startTime = System.currentTimeMillis();
    Object result = null;
    try
    {
      result = pjp.proceed();
      if ((this.indentView) && (this.showArgsAndResultValue))
      {
        buf.append(" Return: ");
        appendReturnValue(buf, result);
      }
    }
    catch (Throwable ex)
    {
      if ((this.indentView) && (this.showArgsAndResultValue)) {
        buf.append(" Throwable: ").append(ex);
      }
      throw ex;
    }
    finally
    {
      if (this.indentView) {
        printEnd(proxyClassName, startTime, buf);
      } else {
        printRap(proxyClassName, startTime, pjp);
      }
    }
    return result;
  }
  
  protected void printRap(String proxyClassName, long startTime, ProceedingJoinPoint pjp)
  {
    if (this.simpleClassName) {
      LOGGER.log((Marker)null, proxyClassName, 20, MessageFormatter.format("[{}ms] {}", Long.valueOf(System.currentTimeMillis() - startTime), pjp.getSignature().toShortString()).getMessage(), null, null);
    } else {
      LOGGER.log(null, proxyClassName, 20, MessageFormatter.format("[{}ms] {}", Long.valueOf(System.currentTimeMillis() - startTime), pjp.getSignature().toLongString()).getMessage(), null, null);
    }
  }
  
  protected void printBegin(String proxyClassName, String methodName, ProceedingJoinPoint pjp)
  {
    StringBuilder buf = new StringBuilder(64);
    buf.append(methodName);
    buf.append("(");
    if (this.showArgsAndResultValue) {
      appendArguments(buf, pjp.getArgs());
    }
    buf.append(")");
    

    LOGGER.log(null, proxyClassName, 10, MessageFormatter.format("BEGIN: {}", buf).getMessage(), null, null);
    if (MDC.get(this.mdcIndent) != null) {
      MDC.put(this.mdcIndent, MDC.get(this.mdcIndent) + this.indent);
    } else {
      MDC.put(this.mdcIndent, this.indent);
    }
  }
  
  protected void printEnd(String proxyClassName, long startTime, StringBuilder buf)
  {
    MDC.put(this.mdcIndent, MDC.get(this.mdcIndent).substring(this.indent.length()));
    

    LOGGER.log(null, proxyClassName, 10, MessageFormatter.format("END: [{}ms] {}", Long.valueOf(System.currentTimeMillis() - startTime), buf).getMessage(), null, null);
  }
  
  protected void appendArguments(StringBuilder buf, Object[] arguments)
  {
    appendObject(buf, arguments);
  }
  
  protected void appendReturnValue(StringBuilder buf, Object returnValue)
  {
    appendObject(buf, returnValue);
  }
  
  protected void appendObject(StringBuilder buf, Object object)
  {
    if (object == null)
    {
      buf.append("<null>");
    }
    else if (object.getClass().isArray())
    {
      buf.append(object.getClass().getSimpleName());
      buf.append('{');
      int length = ArrayUtils.getLength(object);
      if (length > 0)
      {
        for (int i = 0; i < length; i++)
        {
          if (buf.length() > this.maxLengthOfValue)
          {
            buf.setLength(this.maxLengthOfValue - 3);
            buf.append("...");
            return;
          }
          appendObject(buf, Array.get(object, i));
          buf.append(", ");
        }
        buf.setLength(buf.length() - 2);
      }
      buf.append('}');
    }
    else if ((object instanceof String))
    {
      buf.append("'").append(object).append("'");
    }
    else if (ClassUtils.isPrimitiveOrWrapper(object.getClass()))
    {
      buf.append(object);
    }
    else if ((object instanceof Date))
    {
      buf.append("'");
      buf.append(DateFormat.getDateTimeInstance().format((Date)object));
      buf.append("'");
    }
    else if ((object instanceof DateTime))
    {
      buf.append("'");
      buf.append(((DateTime)object).toString(DateTimeFormat.mediumDateTime()));
      buf.append("'");
    }
    else if (this.excludeClassSet.contains(object.getClass().getName()))
    {
      buf.append(ReflectionToStringBuilder.toString(object, ToStringStyle.SHORT_PREFIX_STYLE, false, false, object.getClass()));
    }
    else
    {
      buf.append(ReflectionToStringBuilder.toString(object, ToStringStyle.SHORT_PREFIX_STYLE));
    }
    if (buf.length() > this.maxLengthOfValue)
    {
      buf.setLength(this.maxLengthOfValue - 3);
      buf.append("...");
    }
  }
  
  public boolean isIndentView()
  {
    return this.indentView;
  }
  
  public void setIndentView(boolean indentView)
  {
    this.indentView = indentView;
  }
  
  public boolean isSimpleClassName()
  {
    return this.simpleClassName;
  }
  
  public void setSimpleClassName(boolean simpleClassName)
  {
    this.simpleClassName = simpleClassName;
  }
  
  public boolean isShowArgsAndResultValue()
  {
    return this.showArgsAndResultValue;
  }
  
  public void setShowArgsAndResultValue(boolean showArgsAndResultValue)
  {
    this.showArgsAndResultValue = showArgsAndResultValue;
  }
  
  public int getMaxLengthOfValue()
  {
    return this.maxLengthOfValue;
  }
  
  public void setMaxLengthOfValue(int maxLengthOfValue)
  {
    this.maxLengthOfValue = maxLengthOfValue;
  }
  
  public String getIndent()
  {
    return this.indent;
  }
  
  public void setIndent(String indent)
  {
    this.indent = indent;
  }
  
  public String getMdcIndent()
  {
    return this.mdcIndent;
  }
  
  public void setMdcIndent(String mdcIndent)
  {
    this.mdcIndent = mdcIndent;
  }
  
  public Set<String> getExcludeClassSet()
  {
    return this.excludeClassSet;
  }
  
  public void setExcludeClassSet(Set<String> excludeClassSet)
  {
    this.excludeClassSet = excludeClassSet;
  }
}
