package io.magentys.cherry.reactive.models;

/**
 * Holds failure reason
 */
@SuppressWarnings("WeakerAccess")
public class Failure
{
  private final Object content;

  public Failure(Object content)
  {
    this.content = content;
  }

  public static Failure failure(final Object content)
  {
    return new Failure(content);
  }

  public <TYPE> TYPE getContentAs(Class<TYPE> clazz)
  {
    return clazz.cast(content);
  }

  public Object getContent()
  {
    return content;
  }

  public static Failure empty()
  {
    return new Failure("");
  }

  public boolean equals(final Object o)
  {
    if (o instanceof Failure)
    {
      Failure failure = (Failure) o;
      Object failureContent = failure.getContent();
      return failureContent == null
        && content == null || failureContent != null
        && content != null
        && failureContent.equals(content);
    }
    return false;
  }

}
