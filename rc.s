/*
 * Generated Fri Jan 09 00:23:58 PST 2015
 */

	.section ".rodata"
	.align 4
_endl:	.asciz "\n"
_intFmt:	.asciz "%d"
_strFmt:	.asciz "%s"
_boolT:	.asciz "true"
_boolF:	.asciz "false"
arrrray_error:	.asciz "Index value of %d is outside legal range [0,%d).\n"
del_errrror:	.asciz "Attempt to dereference NULL pointer.\n"

	.section ".data"
	.align 4
.globalInit:	.word 0

	.section ".rodata"
	.align 4
_float_5.50:	.single 0r5.5

	.section ".text"
	.align 4
	set			_float_5.50, %l1
	ld			[%l1], %l1
	mov			%fp-, %l7
	st			%l1, [%l7]
!FloatType@7442df79 f = 5.5
	.section ".data"
	.align 4
f:	.single 0r0

	mov			%fp-, %l3
	ld			[%l3], %l5
	st			%l5, [%l0]
!f = 5.5
!IntType@525dbc90 f

!IntType@525dbc90 f

!FloatType@806bc2f i

!FloatType@806bc2f i

	.section ".rodata"
	.align 4
_float_4.41:	.single 0r4.4

	.section ".text"
	.align 4
	set			_float_4.41, %l6
	ld			[%l6], %l6
	mov			%fp-12, %l4
	st			%l6, [%l4]
	mov			%fp-8, %l1
	mov			%fp-12, %l0
	ld			[%l0], %l3
	st			%l3, [%l1]
!i = 4.4
!i

	set			4, %l7
	mov			%fp-16, %l4
	st			%l7, [%l4]
	mov			%fp-8, %l2
	mov			%fp-16, %l3
	ld			[%l3], %f0
	fitos		%f0, %f0
	mov			%fp-8, %l0
	st			%f0, [%l0]
	st			%f0, [%l2]
!i = 4
!i

