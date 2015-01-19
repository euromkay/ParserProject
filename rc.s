/*
 * Generated Mon Jan 19 14:00:16 PST 2015
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

	.section ".data"
	.align 4
i:	.word 0

	.section ".data"
	.align 4
j:	.word 0

! ---f---
	.section ".text"
	.align 4
	.global f
f:
	set			SAVE.f, %g1
	save		%sp, %g1, %sp

	mov			%fp, %l4
	set			4, %l3
	sub			%l4, %l3, %l3
	st			%i0, [%l3]
	mov			%fp, %l2
	set			8, %l1
	sub			%l2, %l1, %l1
	st			%i1, [%l1]


!i + j
	mov			%fp, %l1
	set			4, %l0
	sub			%l1, %l0, %l0
	ld			[%l0], %o0
	mov			%fp, %l1
	set			8, %l0
	sub			%l1, %l0, %l0
	ld			[%l0], %o1
	add			%o0, %o1, %o0
	mov			%fp, %l1
	set			12, %l0
	sub			%l1, %l0, %l0
	st			%o0, [%l0]

	mov			%fp, %l1
	set			12, %l0
	sub			%l1, %l0, %l0
	ld			[%l0], %i0
	ret
	restore


	ret
	restore



	SAVE.f = -(92 + 12) & -8
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

!bool i
!bool j
	set			.globalInit, %l0
	set			1, %l1
	st			%l1, [%l0]
.globalFinish:

!0
	set			0, %l0
	mov			%fp, %l2
	set			4, %l1
	sub			%l2, %l1, %l1
	st			%l0, [%l1]

	mov			%fp, %l1
	set			4, %l0
	sub			%l1, %l0, %l0
	ld			[%l0], %i0
	ret
	restore


	ret
	restore



	SAVE.main = -(92 + 4) & -8
