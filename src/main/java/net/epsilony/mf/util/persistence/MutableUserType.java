package net.epsilony.mf.util.persistence;

import java.io.Serializable;

import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.HibernateException;
import org.hibernate.type.SerializationException;
import org.hibernate.usertype.UserType;

/**
 * A skeleton Hibernate {@link UserType}. Assumes, by default, that the return type is mutable. Subtypes whose
 * {@code deepCopy} implementation returns a non-serializable object <strong>must override</strong>
 * {@link #disassemble(Object)}.
 * <p>
 * User types returning only <em>im</em>mutable objects should extend {@link ImmutableUserType}.
 *
 * @author anph
 * @since 25 Feb 2009
 *
 */
public abstract class MutableUserType implements UserType {

    /* (non-Javadoc)
     * @see org.hibernate.usertype.UserType#isMutable()
     */
    @Override
    public boolean isMutable() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.hibernate.usertype.UserType#equals(java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return ObjectUtils.equals(x, y);
    }

    /* (non-Javadoc)
     * @see org.hibernate.usertype.UserType#hashCode(java.lang.Object)
     */
    @Override
    public int hashCode(Object x) throws HibernateException {
        assert (x != null);
        return x.hashCode();
    }

    /* (non-Javadoc)
     * @see org.hibernate.usertype.UserType#assemble(java.io.Serializable, java.lang.Object)
     */
    @Override
    public Object assemble(Serializable cached, Object owner)
            throws HibernateException {
        // also safe for mutable objects
        return deepCopy(cached);
    }

    /**
     * Disassembles the object in preparation for serialization. See
     * {@link org.hibernate.usertype.UserType#disassemble(java.lang.Object)}.
     * <p>
     * Expects {@link #deepCopy(Object)} to return a {@code Serializable}.
     * <strong>Subtypes whose {@code deepCopy} implementation returns a non-serializable object must override this
     * method.</strong>
     */
    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        // also safe for mutable objects
        Object deepCopy = deepCopy(value);

        if (!(deepCopy instanceof Serializable)) {
            throw new SerializationException(
                    String.format("deepCopy of %s is not serializable", value), null);
        }

        return (Serializable) deepCopy;
    }

    /* (non-Javadoc)
     * @see org.hibernate.usertype.UserType#replace(java.lang.Object, java.lang.Object, java.lang.Object)
     */
    @Override
    public Object replace(Object original, Object target, Object owner)
            throws HibernateException {
        // also safe for mutable objects
        return deepCopy(original);
    }
}
