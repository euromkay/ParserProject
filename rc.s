/*
 * Generated Wed Jan 14 02:18:46 PST 2015
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

!5
	set			5, %l1
	mov			%fp, %l2
	set			4, %l3
	sub			%l2, %l3, %l3
	st			%l1, [%l3]

!float[5] f
	.section ".data"
	.align 4
f:	.word 0
! ---main---
	.section ".text"
	.align 4
	.global main

main:
	set			SAVE.main, %g1

	set			.globalInit, %l0
	ld			[%l0], %l0
	cmp			%l0, %g0
	bne			.globalFinish
	nop

	set			.globalInit, %l0
	set			1, %l4
	st			%l4, [%l0]
.globalFinish:


!0
	set			0, %l5
	mov			%fp-8, %l2
	set			8, %l7
	sub			%l2, %l7, %l7
	st			%l5, [%l7]

!int i = 0
	mov			%fp-8, %l4
	set			8, %l0
	sub			%l4, %l0, %l0
	ld			[%l0], %l3
	st			%l3, [%l1]
!i = 0
!int i = 0

	set			0, %l6
	mov			%fp-16, %l7
	set			16, %l2
	sub			%l7, %l2, %l2
	st			%l6, [%l2]
.for0:
	set			5, %l5
	mov			%fp-16, %l0
	set			16, %l4
	sub			%l0, %l4, %l4
	ld			[%l4], %o1
	cmp			%o1, %l5
	be			.for0.DONE
	nop
	set			4, %o0
	call		.mul
	nop
	mov			f, %l3
	ld			[%l3], %l1
	mov			%fp-20, %l6
	set			20, %l2
	sub			%l6, %l2, %l2
	st			%l1, [%l2]
!i P+ i
	mov			%fp-12, %l4
	set			12, %l0
	sub			%l4, %l0, %l0
	ld			[%l0], %o0
	mov			%fp-24, %l7
	set			24, %l5
	sub			%l7, %l5, %l5
	st			%o0, [%l5]
	inc			%o0
	mov			%fp-12, %l6
	set			12, %l3
	sub			%l6, %l3, %l3
	st			%o0, [%l3]

	mov			%fp-20, %l1
	set			20, %l2
	sub			%l1, %l2, %l2
	mov			%fp-24, %l7
	set			24, %l0
	sub			%l7, %l0, %l0
	ld			[%l0], %f0
	fitos		%f0, %f0
	mov			%fp-20, %l5
	set			20, %l4
	sub			%l5, %l4, %l4
	st			%f0, [%l4]
	st			%f0, [%l2]
!f = iP+
!f

.for0CHECK:
	mov			%fp-16, %l1
	set			16, %l3
	sub			%l1, %l3, %l3
	ld			[%l3], %l6
	inc			%l6
	st			%l6, [%fp-16]
	ba			.for0
	nop
.for0.DONE:

!foreach(float f : f)

	set			0, %l7
	mov			%fp-36, %l5
	set			36, %l0
	sub			%l5, %l0, %l0
	st			%l7, [%l0]
.for2:
	set			5, %l4
	mov			%fp-36, %l1
	set			36, %l2
	sub			%l1, %l2, %l2
	ld			[%l2], %o1
	cmp			%o1, %l4
	be			.for2.DONE
	nop
	set			4, %o0
	call		.mul
	nop
	mov			f, %l6
	ld			[%l6], %l3
	ld			[%l3], %l3
	mov			%fp-40, %l7
	set			40, %l0
	sub			%l7, %l0, %l0
	st			%l3, [%l0]
	mov			%fp-40, %l2
	set			40, %l1
	sub			%l2, %l1, %l1
	ld			[%l1], %f0
	call		printFloat
	nop
	set			_strFmt, %o0
	set			_endl, %o1
	call		printf
	nop
!cout << f << endl

.for2CHECK:
	mov			%fp-36, %l6
	set			36, %l5
	sub			%l6, %l5, %l5
	ld			[%l5], %l4
	inc			%l4
	st			%l4, [%fp-36]
	ba			.for2
	nop
.for2.DONE:

!foreach(float f : f)

!1
	set			1, %l7
	mov			%fp-48, %l3
	set			48, %l0
	sub			%l3, %l0, %l0
	st			%l7, [%l0]

	mov			%fp-48, %l1
	set			48, %l2
	sub			%l1, %l2, %l2
	ld			[%l2], %o0
	call		exit
	nop
!exit(1)

	ret
	restore



	SAVE.main = -(92 + 48) & -8
