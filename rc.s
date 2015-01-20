/*
 * Generated Mon Jan 19 16:35:59 PST 2015
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

! ---main---
	.section ".text"
	.align 4
	.global main
main:
	set			SAVE.main, %g1
	save		%sp, %g1, %sp

	set			.globalInit, %l0
	ld			[%l0], %l0
	cmp			%l0, %g0
	bne			.globalFinish
	nop

	set			.globalInit, %l0
	set			1, %l1
	st			%l1, [%l0]
.globalFinish:

!42
	set			42, %l0
	mov			%fp, %l2
	set			4, %l1
	sub			%l2, %l1, %l1
	st			%l0, [%l1]

!const int ci = 42
	mov			%fp, %l3
	set			4, %l2
	sub			%l3, %l2, %l2
	ld			[%l2], %l0
	st			%l0, [%l1]

!ci + ci
	mov			%fp, %l1
	set			8, %l0
	sub			%l1, %l0, %l0
	ld			[%l0], %o0
	add			%o0, %o1, %o0
	mov			%fp, %l1
	set			12, %l0
	sub			%l1, %l0, %l0
	st			%o0, [%l0]

!ci * (ci + ci)
	mov			%fp, %l1
	set			8, %l0
	sub			%l1, %l0, %l0
	ld			[%l0], %o0
	mov			%fp, %l1
	set			12, %l0
	sub			%l1, %l0, %l0
	ld			[%l0], %o1
	call		.mul
	nop
	mov			%fp, %l1
	set			16, %l0
	sub			%l1, %l0, %l0
	st			%o0, [%l0]

!5
	set			5, %l0
	mov			%fp, %l2
	set			20, %l1
	sub			%l2, %l1, %l1
	st			%l0, [%l1]

!9
	set			9, %l0
	mov			%fp, %l2
	set			24, %l1
	sub			%l2, %l1, %l1
	st			%l0, [%l1]

!3
	set			3, %l0
	mov			%fp, %l2
	set			28, %l1
	sub			%l2, %l1, %l1
	st			%l0, [%l1]

!9 / 3
	mov			%fp, %l1
	set			24, %l0
	sub			%l1, %l0, %l0
	ld			[%l0], %o0
	mov			%fp, %l1
	set			28, %l0
	sub			%l1, %l0, %l0
	ld			[%l0], %o1
	call		.div
	nop
	mov			%fp, %l1
	set			32, %l0
	sub			%l1, %l0, %l0
	st			%o0, [%l0]

!4
	set			4, %l0
	mov			%fp, %l2
	set			36, %l1
	sub			%l2, %l1, %l1
	st			%l0, [%l1]

!9 / 34
	mov			%fp, %l1
	set			32, %l0
	sub			%l1, %l0, %l0
	ld			[%l0], %o0
	mov			%fp, %l1
	set			36, %l0
	sub			%l1, %l0, %l0
	ld			[%l0], %o1
	call		.rem
	nop
	mov			%fp, %l1
	set			40, %l0
	sub			%l1, %l0, %l0
	st			%o0, [%l0]

!5 / (9 / 34)
	mov			%fp, %l1
	set			20, %l0
	sub			%l1, %l0, %l0
	ld			[%l0], %o0
	mov			%fp, %l1
	set			40, %l0
	sub			%l1, %l0, %l0
	ld			[%l0], %o1
	call		.div
	nop
	mov			%fp, %l1
	set			44, %l0
	sub			%l1, %l0, %l0
	st			%o0, [%l0]

!ci * (ci + ci) - 5 / (9 / 34)
	mov			%fp, %l1
	set			16, %l0
	sub			%l1, %l0, %l0
	ld			[%l0], %o0
	mov			%fp, %l1
	set			44, %l0
	sub			%l1, %l0, %l0
	ld			[%l0], %o1
	sub			%o0, %o1, %o0
	mov			%fp, %l1
	set			48, %l0
	sub			%l1, %l0, %l0
	st			%o0, [%l0]

!const float foo = ci * (ci + ci) - 5 / (9 / 34)
	mov			%fp, %l2
	set			48, %l1
	sub			%l2, %l1, %l1
	ld			[%l1], %f0
	fitos			%f0, %f0
	mov			%fp, %l1
	set			52, %l0
	sub			%l1, %l0, %l0
	st			%f0, [%l0]

!2.3
	.section ".rodata"
	.align 4
_float_2.30:	.single 0r2.3

	.section ".text"
	.align 4
	set			_float_2.30, %l0
	ld			[%l0], %l0
	mov			%fp, %l2
	set			56, %l1
	sub			%l2, %l1, %l1
	st			%l0, [%l1]

!foo / 2.3
	mov			%fp, %l1
	set			52, %l0
	sub			%l1, %l0, %l0
	ld			[%l0], %f0
	mov			%fp, %l1
	set			56, %l0
	sub			%l1, %l0, %l0
	ld			[%l0], %f1
	fdivs		%f0, %f1, %f0
	mov			%fp, %l1
	set			60, %l0
	sub			%l1, %l0, %l0
	st			%f0, [%l0]

!foo / 2.3 + foo
	mov			%fp, %l1
	set			60, %l0
	sub			%l1, %l0, %l0
	ld			[%l0], %f0
	mov			%fp, %l1
	set			52, %l0
	sub			%l1, %l0, %l0
	ld			[%l0], %f1
	fadds		%f0, %f1, %f0
	mov			%fp, %l1
	set			64, %l0
	sub			%l1, %l0, %l0
	st			%f0, [%l0]

!const float bar = foo / 2.3 + foo
	mov			%fp, %l3
	set			64, %l2
	sub			%l3, %l2, %l2
	ld			[%l2], %l0
	st			%l0, [%l1]

!0
	set			0, %l0
	mov			%fp, %l2
	set			72, %l1
	sub			%l2, %l1, %l1
	st			%l0, [%l1]

	mov			%fp, %l1
	set			72, %l0
	sub			%l1, %l0, %l0
	ld			[%l0], %i0
	ret
	restore


	ret
	restore



	SAVE.main = -(92 + 72) & -8
